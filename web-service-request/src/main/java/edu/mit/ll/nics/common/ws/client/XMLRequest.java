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
package edu.mit.ll.nics.common.ws.client;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class XMLRequest extends WebServiceRequest<Object>{
	
	public static String DOCUMENT_FORMAT = "DOCUMENT";
	public static String STRING_FORMAT = "STRING";
	private String format = DOCUMENT_FORMAT;
	
	public XMLRequest(String format){
		if(this.validFormat(format)){
			this.format = format;
		}
	}
	
	public XMLRequest(){}
	
	/** parseRequest and return an XML Document or XML String
	 * @param InputStream is - input stream returned from requestData
	 * @return Document or String or null if an exception was thrown
	 */
	protected Object parseRequest(InputStream is){
		if(this.format.equalsIgnoreCase(DOCUMENT_FORMAT)){
			return this.parseXML(is);
		}else{
			return this.getResponseString(is);
		}
	}
	/** parseXML return XML Document
	 * @param InputStream is - input stream returned from requestData
	 * @return Document or null if an exception was thrown
	 */
	protected Document parseXML(InputStream is){
		Document document = null;
		try{
			DocumentBuilder builder = this.getDocumentBuilder();
			
			if(builder != null){
				document = builder.parse(is);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return document;
	}
	
	/** Get XML
	 * @param InputStream is
	 * @return String XML
	 */
	public String getResponseString(InputStream is){
		StringBuffer result = new StringBuffer();
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String inputLine;
			while ((inputLine = in.readLine()) != null){
				result.append(inputLine + '\n');
			}
			in.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result.toString();
	}
	
	/** Parse XML File
	 * @param File f 
	 * @return Document or null if an exception was thrown
	 */
	public Document parseXMLFile(File f){
		Document document = null;
		try{
			DocumentBuilder builder = this.getDocumentBuilder();
			
			if(builder != null){
				document = builder.parse(f);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return document;
	}
	/** getDocumentBuilder
	 * @return DocumentBuilder
	 */
	private DocumentBuilder getDocumentBuilder(){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(false);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			
			return factory.newDocumentBuilder();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/** validFormat
	 * @param format - current support document and string
	 * @return boolean
	 */
	private boolean validFormat(String format){
		if(format.equalsIgnoreCase(DOCUMENT_FORMAT)){
			return true;
		}else if(format.equalsIgnoreCase(STRING_FORMAT)){
			 return true;
		}
		return false;
	}

}