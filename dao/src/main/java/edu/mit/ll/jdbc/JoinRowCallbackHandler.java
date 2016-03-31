/**
 * Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.mit.ll.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

public class JoinRowCallbackHandler<T> implements RowCallbackHandler {

    private Logger log = LoggerFactory.getLogger(JoinRowCallbackHandler.class);

    private List<T> results;
    private JoinRowMapper<T> mainMapper;
    private JoinRowMapper[] additionalMappers;

    public JoinRowCallbackHandler(JoinRowMapper<T> mainMapper, JoinRowMapper... additionalMappers) {
        this.results = new ArrayList<T>();
        this.mainMapper = mainMapper;
        this.additionalMappers = additionalMappers;
    }

    
    public void processRow(ResultSet rs) throws SQLException {
        int rowNum = rs.getRow();
        
        T mainObject = mainMapper.getExistingObject(rs);
        
        if(mainObject == null) {
            // Get the main object we're querying against
            mainObject = mainMapper.mapRow(rs, rowNum);
            results.add(mainObject);
        }
        
        // At this point rootObject is in the results list
        // so we should run the additional JoinRowMappers against the ResultSet
        handleAdditionalMappers(additionalMappers, mainObject, rs, rowNum);
    }    


    public final List<T> getResults() {
        return this.results;
    }

    public final T getSingleResult() {
        if(this.results.size() < 1) {
            return null;
        }
        else {
            return this.results.get(0);
        }
    }

    private void handleAdditionalMappers(JoinRowMapper[] additionalMappers, Object rootObject, ResultSet rs, int rowNum) throws SQLException {
        Method[] methods = rootObject.getClass().getMethods();

        for (int i = 0; i < additionalMappers.length; i++) {
            JoinRowMapper mapper = additionalMappers[i];
            Object mappedObject = mapper.getExistingObject(rs);
            if(mappedObject == null){		
            		mappedObject = mapper.mapRow(rs, rowNum);
            }
            if(mappedObject != null){ //Check to make sure that the mapping returned a valid object
	            Class mappedClass = mappedObject.getClass();
	            Method method = findMatchingSetter(methods, mappedClass, rootObject);
	            if(method != null) {
	                trySetting(method, rootObject, mappedObject);
	            }
	            else {
	                throw new SQLException("Couldn't find a setter for the object [" + mappedClass.getSimpleName() + "]");
	            }
	
	            // If this JoinRowMapper has additionalMappers attached, use them now
	            List<JoinRowMapper> newMappers = mapper.getAdditionalMappers();
	            if(!newMappers.isEmpty()) {
	                handleAdditionalMappers(newMappers.toArray(new JoinRowMapper[newMappers.size()]), mappedObject, rs, rowNum);
	            }
            }
        }
    }

    private Method findMatchingSetter(Method[] methods, Class paramClass, Object mainObject) {
        // Find a method that is a setter for an object matching the class
        // of the paramClass
        if(log.isDebugEnabled()) {
            log.debug("Looking for a setter for " + paramClass.toString());
        }
       for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String methodName = method.getName();
            Class[] paramTypes = method.getParameterTypes();
            if(methodName.startsWith("set") && paramTypes.length == 1) {
                Class paramType = paramTypes[0];
                if(log.isDebugEnabled()) {
                    log.debug("Found a setter [" + method.toString() + "]");
                }

                if(paramClass.equals(paramType)) {
                    return method;
                }
                // Also check if its a setter for a Collection matching the paramClass
                else if(Collection.class.isAssignableFrom(paramType) && 
                		findMatchingCollectionSetter(method.getName(), paramClass, mainObject)) {
                    return method;
                }                
            }
        }
        return null;
    }
    
    private boolean findMatchingCollectionSetter(String methodName, Class paramClass, Object mainObject){
    	//Get the field name of the method by stripping off the beginning "set" and changing the first letter to lower case
    	String fieldName = methodName.substring(3, 5).toLowerCase() + methodName.substring(5, methodName.length());
    	try{
    		Field field = mainObject.getClass().getDeclaredField(fieldName);
    		//The field is a collection - so get the type
        	if(field != null){
        		ParameterizedType type = (ParameterizedType) field.getGenericType();
            	
            	Class listClass  = (Class<?>) type.getActualTypeArguments()[0];
            	
            	//Check to see if the type in the array list is the same as the object we're trying to set
            	if(listClass.equals(paramClass)){
            		return true;
            	}
            }
    	}catch(Exception e){
    		log.debug("No field found in #0 with name #1", mainObject.getClass().toString(), fieldName);
    	}
    	return false;
    }

    private <W> void trySetting(Method method, W thisObject, Object parameterObject) throws SQLException {
    	Class paramType = method.getParameterTypes()[0];
        if(Collection.class.isAssignableFrom(paramType)) {
            // If the setter is looking for a Collection, then we don't actually
            // know if this is the right setter due to type erasure. We'll have to
            // try setting and catch the exception if it's the wrong type. In order
            // to add to the Collection we'll first have to look up the getter for
            // this field, get the existing Collection (if it exists, otherwise
            // create it), and then add to the Collection and call the setter with it.
            String getterName = method.getName().replaceFirst("set", "get");
            try {
                Method getterMethod = thisObject.getClass().getMethod(getterName);
                Collection collection = (Collection) getterMethod.invoke(thisObject);
                if(collection == null) {
                    collection = new ArrayList();
                }
                collection.add(parameterObject);
                method.invoke(thisObject, collection);
                if(log.isDebugEnabled()) {
                    log.debug("Was able to set Collection using setter " + method.toString());
                }
                return; // If we make it this far, the set worked
            }
            catch(Exception e) {
                log.debug("Exception while trying to set [" + 
                    parameterObject.getClass().getSimpleName() + "] using " +
                    method.toString(), e);
            }
        }
        else {
            // If setter is not looking for a Collection, then use it normally
            try {
                method.invoke(thisObject, parameterObject);
                return; // If we make it this far, the set worked
            }
            catch(Exception e) {
                throw new SQLException("Couldn't invoke setter [" + 
                    method.getName() + "] on " + 
                    thisObject.getClass().getSimpleName() + " with " + 
                    parameterObject.getClass().getSimpleName());
            }
        }

        // If we make it here, then none of the setters worked, so throw an exception
        throw new SQLException("Couldn't find a setter for the object [" + parameterObject.getClass().getSimpleName() + "]");
    }
}
