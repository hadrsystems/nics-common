/**
 * Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.nics.common.messages.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import edu.mit.ll.nics.common.messages.ParserUtil;
import edu.mit.ll.nics.common.messages.sadisplay.SADisplayMessage;

public final class PopulateEntityHelper {

    /** The log. */
    private static Logger log = Logger.getLogger(SADisplayMessageParser.class.getSimpleName());
    private static WKTReader reader = new WKTReader();

    /**setField
     * sets the value of the given field on the given object
     * @param c - the object class
     * @param methodName - the setter method
     * @param parameterType - the type of the value being set
     * @param object - the object the field is being set on
     * @param value - the field value
     * @return SADisplayMessage object that contains the data from the passed string
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws SADisplayParserException 
     * @throws IllegalArgumentException 
     * @throws ParseException 
     */
    public static void setField(Class<?> c, String methodName, Class<?> parameterType, Object object, Object value) 
      throws IllegalArgumentException, SADisplayParserException, IllegalAccessException, InvocationTargetException, ParseException {
        Method method = null;
    	try {
            method = c.getDeclaredMethod(methodName, parameterType);
        } catch (NoSuchMethodException me) {
            try{
            	method = c.getMethod(methodName, parameterType);
            }catch(NoSuchMethodException me2){
            	return;
            }
        }
        if(method != null){
        	method.invoke(object, castValue(value, parameterType));
        }
    }
    
    /**setField
     * sets the value of the given field on the given object
     * @param c - the object class
     * @param methodName - the setter method
     * @param parameterType - the type of the value being set
     * @param object - the object the field is being set on
     * @param value - the field value
     * @return SADisplayMessage object that contains the data from the passed string
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws SADisplayParserException 
     * @throws IllegalArgumentException 
     * @throws ParseException 
     */
    public static Object getField(Class<?> c, String methodName, Object object) 
      throws IllegalArgumentException, SADisplayParserException, IllegalAccessException, InvocationTargetException, ParseException {
        Method method = null;
    	try {
            method = c.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException me) {
            try{
            	method = c.getMethod(methodName);
            }catch(NoSuchMethodException me2){
            	return null;
            }
        }
        if(method != null){
        	return method.invoke(object);
        }
        return null;
    }

    /**castValue
     * cast the value to the type indicated in the set method
     * @param value
     * @param classType - the type the value should be casted to
     * @return Object - newly casted object
     * @throws ParseException 
     */
    private static Object castValue(Object value, Class<?> classType) throws ParseException, SADisplayParserException{
    	if(value.getClass().equals(String.class) &&
    			((String)value).equals(SADisplayMessage.CLEAR_FIELD)){
    		return null;
    	}
    	if (castObject(value, classType)) {
            if (classType.isPrimitive()) {
                if (classType.equals(long.class)) {
                    if (isInt(value)) {
                        return ((Integer) value).longValue();
                    }
                } else if (classType.equals(int.class)) {
                    if (isLong(value)) {
                        return ((Long) value).intValue();
                    } else if (isString(value)) {
                        return (new Integer((String) value)).intValue();
                    }
                } else if (classType.equals(double.class)) {
                    if (isLong(value)) {
                        return ((Long) value).doubleValue();
                    }
                    if (isInt(value)) {
                        return ((Integer) value).doubleValue();
                    }
                    if (isString(value)) {
                        return (new Double((String) value)).doubleValue();
                    }
                } else if (classType.equals(boolean.class)) {
                    if (isString(value)) {
                        if (value.equals("true")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else if (classType.equals(Integer.class)) {
                    if (isInt(value)) {
                        return (Integer) value;
                    }
                } else if (classType.equals(byte[].class)){
                	if(isString(value)){
                		return ((String) value).getBytes();
                	}
                }
            } else {
                if (classType.equals(Integer.class)) {
                    if (isString(value)) {
                        return (new Integer((String) value));
                    }
                } else if (classType.equals(Long.class)) {
                    if (isInt(value)) {
                        return new Long((Integer) value);
                    } else if (isString(value)) {
                        return new Long((String) value);
                    }
                } else if (classType.equals(Double.class)) {
                    if (isInt(value)) {
                        return new Double((Integer) value);
                    } else if (isString(value)) {
                        return new Double((String) value);
                    }
                } else if (classType.equals(Map.class)) {
                    if (isJSONObject(value)) {
                        return ParserUtil.jsonToMap((JSONObject) value);
                    }
                } else if (classType.equals(Geometry.class)) {
                    if (isString(value)) {
                        return parseWellKnownText((String) value);
                    }
                } else if (classType.equals(Boolean.class)) {
                    if (isString(value)) {
                        if (value.equals("true")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else if (classType.equals(Date.class)) {
                    if (isString(value)) {
                    	String pattern1 = "yyyy-MM-dd hh:mm:ss";
                        String pattern2 = "EEE MMM dd HH:mm:ss z yyyy";
                        String pattern = "";
                    	if(((String) value).length() == pattern1.length()){
                    		pattern = pattern1;
                    	}else{
                    		pattern = pattern2;
                    	}
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        Date d = sdf.parse((String) value);
                        return d;
                    }
                } else if (classType.equals(Polygon.class)) {
                    if (isString(value)) {
                        return (Polygon) parseWellKnownText((String) value);
                    }
                }else if (classType.equals(byte[].class)){
                	if(isString(value)){
                		return ((String) value).getBytes();
                	}
                }
            }
    	}
        return value;
    }

    /**isInt
     * returns boolean value indicating whether the value is an int
     * @param value
     * @return boolean
     */
    private static boolean isInt(Object value) {
        return (value instanceof Integer || value.getClass().equals(int.class));
    }

    /**isLong
     * returns boolean value indicating whether the value is a long
     * @param value
     * @return boolean
     */
    private static boolean isLong(Object value) {
        return (value instanceof Long || value.getClass().equals(long.class));
    }

    /**isString
     * returns boolean value indicating whether the value is a String
     * @param value
     * @return boolean
     */
    private static boolean isString(Object value) {
        return value.getClass().equals(String.class);
    }

    /**isJSONObject
     * returns boolean value indicating whether the value is a JSONObject
     * @param value
     * @return boolean
     */
    private static boolean isJSONObject(Object value) {
        return value.getClass().equals(JSONObject.class);
    }

    /**castObject
     * returns boolean value indicating whether the value need to be cast
     * @param value
     * @param toClass - class to be cast to
     * @return boolean
     */
    private static boolean castObject(Object fromObj, Class<?> toClass) {
    	if (toClass.equals(fromObj.getClass())) {
            return false;
        } else if (toClass.isPrimitive()) {
            if (toClass.equals(long.class) && fromObj.getClass().equals(Long.class)) {
                return false;
            } else if (toClass.equals(int.class) && fromObj.getClass().equals(Integer.class)) {
                return false;
            } else if (toClass.equals(double.class) && fromObj.getClass().equals(Double.class)) {
                return false;
            }
        }
        return true;
    }

    /**
     * prePendSet - preprend "set" to the field name
     * @param methodName
     * @return String - setter method name
     */
    public static String prePendSet(String methodName) {
        StringBuffer nameBuffer = new StringBuffer("set");
        nameBuffer.append(methodName.substring(0, 1).toUpperCase());
        nameBuffer.append(methodName.substring(1, methodName.length()));
        return nameBuffer.toString();
    }
    
    /**
     * prePendSet - preprend "set" to the field name
     * @param methodName
     * @return String - setter method name
     */
    public static String prePendGet(String methodName) {
        StringBuffer nameBuffer = new StringBuffer("get");
        nameBuffer.append(methodName.substring(0, 1).toUpperCase());
        nameBuffer.append(methodName.substring(1, methodName.length()));
        return nameBuffer.toString();
    }

    /**getFieldType
     * returns the class of the given field
     * @param c - Class object of the entity
     * @param key - the field variable contained in the class
     * @return Class - the Class of the field variable
     */
    public static Class<?> getFieldType(Class<?> c, String key) {
        try {
        	return c.getDeclaredField(key).getType();
        } catch (NoSuchFieldException e) {
            try{
            	return c.getField(key).getType();
            }catch (NoSuchFieldException e2) {
            	return null;
            }
        }
    }

    /** parseWellKnownText - translate string to geometry
     *  @param String
     *  @param Geometry
     */
    private static Geometry parseWellKnownText(String wellKnownText) throws SADisplayParserException {
        Geometry geo = null;
        try {
            geo = reader.read(wellKnownText);
        } catch (Exception e) {
        	log.info("<Error Parsing Well Known Text>" + e.getMessage());
        	throw new SADisplayParserException(e.getMessage());
        }
        return geo;
    }
}