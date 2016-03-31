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
package edu.mit.ll.nics.common.alert;

import java.net.InetAddress;
import java.util.Date;

import org.apache.log4j.Logger;

import edu.mit.ll.nics.common.email.EmailFactory;
import edu.mit.ll.nics.common.email.XmlEmail;

public class EmailAlert {

	private long lastEmail = 0;
	private long timeout = 90000;
	private Logger log = Logger.getLogger(EmailAlert.class.getSimpleName());
	private String className;
	private String defFromEmail;
	private String defToEmail;
	private String destUrl = null;
	private EmailFactory ef = null;
	
	/**
	 * constructor
	 * @param toEmail - comma separated list of email addresses to send to
	 * @param fromEmail - email address to send from
	 * @param className - class/app that this message should be associated with
	 */
	protected EmailAlert(String toEmail, String fromEmail, String className, EmailFactory ef) {
		this.defToEmail = toEmail;
		this.defFromEmail = fromEmail;
		this.className = className;
		this.ef = ef;
		log.info("Created LDDRS EmailAlert -- sending from: " + fromEmail);
	}
	
	/**
	 * constructor
	 * @param destUrl - the default camel endpoint to send email xml messages to 
	 * @param toEmail - comma separated list of email addresses to send to
	 * @param fromEmail - email address to send from
	 * @param className - class/app that this message should be associated with
	 */
	protected EmailAlert(String destUrl, String toEmail, String fromEmail, String className, EmailFactory ef){
		this.destUrl = destUrl;
		this.defToEmail = toEmail;
		this.defFromEmail = fromEmail;
		this.className = className;
		this.ef = ef;
		log.info("Created LDDRS EmailAlert -- sending from: " + fromEmail);
	}
	
	/**
	 * Send exception to default topic for alerting
	 * @param e - exception to send alert about
	 */
	public void send(Exception e){
		this.send(e, false);
	}
	
	
	/**
	 * Send exception to default topic for alerting
	 * @param e - exception to send alert about
	 * @param ignoreTimeout - boolean indicating whether we should ignore the timeout value and just send
	 */
	public void send(Exception e, boolean ignoreTimeout){
		this.send(this.defToEmail, e, ignoreTimeout);
	}	
	
	/**
	 * Send exception to default topic for alerting
	 * @param e - exception to send alert about
	 * @param emailTo - email address to send to
	 */
	public void send(String emailTo, Exception e){
		this.send(emailTo, e, false);
	}
	
	
	/**
	 * Send exception to default topic for alerting
	 * @param e - exception to send alert about
	 * @param emailTo - email address to send to
	 * @param ignoreTimeout - boolean indicating whether we should ignore the timeout value and just send
	 */
	public void send(String emailTo, Exception e, boolean ignoreTimeout){
		this.send(this.destUrl, emailTo, e, ignoreTimeout);
	}
	
	
	/**
	 * Send exception to destination for alerting
	 * @param dest - destination camel endpoint
	 * @param emailTo - email address to send to
	 * @param e - exception to send alert about
	 */
	public void send(String dest, String emailTo, Exception e){
		this.send(dest, emailTo, e, false);
	}
	
	/**
	 * Send exception to destination for alerting
	 * @param dest - destination camel endpoint
	 * @param emailTo - email address to send to
	 * @param e - exception to send alert about
	 * @param ignoreTimeout - boolean indicating whether we should ignore the timeout value and just send
	 */
	public void send(String dest, String emailTo, Exception e, boolean ignoreTimeout){
		this.sendString(dest, emailTo, e.getMessage(), ignoreTimeout);
	}
	
	
	/**
	 * Send message to default topic for alerting
	 * @param message - message of alert
	 */
	public void sendString(String message){
		this.sendString(message,false);
	}
	
	/**
	 * Send message to default topic for alerting
	 * @param message - message of alert
	 * @param ignoreTimeout - boolean indicating whether we should ignore the timeout value and just send
	 */
	public void sendString(String message, boolean ignoreTimeout){
		this.sendString(this.defToEmail,message, ignoreTimeout);
	}
	
	/**
	 * Send message to default topic for alerting
	 * @param message - message of alert
	 * @param emailTo - email address to send to
	 */
	public void sendString(String emailTo, String message){
		this.sendString(emailTo, message, false);
	}
	
	/**
	 * Send message to default topic for alerting
	 * @param message - message of alert
	 * @param emailTo - email address to send to
	 * @param ignoreTimeout - boolean indicating whether we should ignore the timeout value and just send
	 */
	public void sendString(String emailTo, String message, boolean ignoreTimeout){
		this.sendString(this.destUrl, emailTo, message, ignoreTimeout);
	}
	
	/**
	 * Send message to destination topic for alerting
	 * @param dest - destination camel endpoint
	 * @param emailTo - email address to send to
	 * @param message - message of alert
	 */
	public void sendString(String dest, String emailTo, String message){
		this.sendString(dest, emailTo, message, false);
	}
	
	/**
	 * Send message to destination topic for alerting
	 * @param dest - destination camel endpoint
	 * @param emailTo - email address to send to
	 * @param message - message of alert
	 * @param ignoreTimeout - boolean indicating whether we should ignore the timeout value and just send
	 */
	public void sendString(String dest, String emailTo, String message, boolean ignoreTimeout){
		log.debug("in emailAlert");
		String hostname = "unknown";
		try{
			hostname = InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e1) {
			log.error("couldn't get hostname",e1);
		}
		
		Date nowDate = new Date();
		long now = nowDate.getTime();
		if(now - this.lastEmail > this.timeout || ignoreTimeout){
			log.debug("sending message to " + dest);
			XmlEmail email = ef.createEmail(defFromEmail, emailTo, "Alert from " + this.className + "@" + hostname);
			email.setBody( nowDate.toString() + "\n\n" + message);
			if(dest != null){
				email.send(dest);
			}
			else{
				email.send();
			}
			if(!ignoreTimeout){
				this.lastEmail = now;
			}
			log.debug("sent message");
		}
		else{
			log.debug("not sending message: doesn't meet timeout criteria");
		}
	}
	
	
	/**
	 * Set timeout for message delay
	 * @param seconds - number of seconds to delay btwn sending messages (to limit spamming alerts). set to 0 for no delay/limiting
	 */
	public void setTimeout(int seconds){
		this.timeout = seconds * 1000;
	}
}
