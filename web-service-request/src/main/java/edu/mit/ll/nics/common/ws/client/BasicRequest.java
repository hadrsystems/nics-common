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
package edu.mit.ll.nics.common.ws.client;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.IOException;

public class BasicRequest extends WebServiceRequest<Object> {

    private Logger log = LoggerFactory.getLogger(BasicRequest.class);
    private String format;
    
    public static String STRING = "string";
    public static String BYTES = "bytes";
    
    public BasicRequest(String format){
		this.format = format;
	}
	
	public BasicRequest(){
		this.format = STRING;
	}
    
    protected Object parseRequest(InputStream is) {
        log.debug("Parsing response...");
        
        if(this.format.toLowerCase().equals(BYTES)){
        	try{
        		return IOUtils.toByteArray(is);
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }else{
	        byte[] bytes = new byte[1024];
	        int read = 0;
	        StringBuffer response = new StringBuffer();
	        
	        try {
	            while(read != -1) {
	                read = is.read(bytes);
	                log.debug("Read " + read + " bytes of response data");
	                if(read > 0) {
	                    response.append(new String(bytes, 0, read));
	                }
	            }
	            is.close();
	        }
	        catch (IOException e) {
	            log.error("Error reading response", e);
	        }
	
	        return response.toString();
        }
        return null;
    
    }

}
