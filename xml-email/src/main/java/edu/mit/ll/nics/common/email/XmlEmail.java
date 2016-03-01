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
package edu.mit.ll.nics.common.email;

import java.io.IOException;
import javax.xml.bind.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.StringWriter;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

/**
 *BUILD AN EMAIL MESSAGE
 */
public class XmlEmail {

    /**
     * <p>Member: CNAME</p>
     * <p>Description:
     * Class name for logging.
     * </p>
     */
    private static final String CNAME = XmlEmail.class.getName();
    /**
     * <p>Member: log</p>
     * <p>Description:
     * The logger.
     * </p>
     */
    private static final Logger log = Logger.getLogger(CNAME);
    /**
     * Default destination url
     * rabbitmq topic on the
     * amqp bus on local machine
     *
     * OLD: "rabbitmq://localhost:5672?amqExchange=amq.topic&amqExchangeType=topic&requestedHeartbeat=0&routingKey=LDDRS.alert.email&noAck=false&user=guest&password=guest&msgPersistent=false&msgContentType=text"
     */
    private String defDstUrl =  "rabbitmq://localhost:5672/iweb.amq.topic?exchangeType=topic&requestedHeartbeat=0&routingKey=iweb.alert.email&autoAck=false&user=guest&password=guest";
    private ObjectFactory of = null;
    private EmailType et = null;
    private Marshaller marsh = null;
    private HeaderType head = null;
    private ContentType content = null;
    private BodyType body = null;
    private ImageType image = null;
    private String dstUrl = null;
    private StringWriter sw = null;
    private File imgFile = null;
    private ProducerTemplate pt = null;

    /**
     * Constructor takes in required email header values and populates a jaxb
     * object that will later be marshalled into an xml string
     * @param from = from email address
     * @param to = to email address(es) Comma separate multiple addresses
     * @param subj = subject of email
     */
    protected XmlEmail(final String from, final String to, final String subj, ProducerTemplate pt) {
        of = new ObjectFactory();
        et = new EmailType();
        head = of.createHeaderType();
        content = of.createContentType();
        body = of.createBodyType();
        image = of.createImageType();
        this.pt = pt;

        //Add header
        head.setFrom(from);
        head.setTo(to);
        head.setSubject(subj);
        et.setHeader(head);

        //Initialize content
        content.setBody(body);
        content.setImage(image);
        et.setContent(content);
    }

    /**
     * Makes the string body_text  the body of the e-mail
     * @param body_text
     */
    public void setBody(final String body_text) {
        setBody(body_text, "text");
    }

    /**
     * Makes the string body_test the body of the e-mail with a tag of the body
     * text format (default = text, i.e. HTML,JSON) to assist later message processing
     * @param body_text
     * @param format
     */
    public void setBody(final String body_text, final String format) {
        body.setText(body_text);
        body.setFormat(format);
        content.setBody(body);
        et.setContent(content);
    }

    /**
     * Puts the addresses in string cc on the cc address list
     * @param cc
     */
    public void setCC(final String cc) {
        head.setCc(cc);
    }

    /**
     * Puts the addresses in string bcc on the bcc address list
     * @param bcc
     */
    public void setBCC(final String bcc) {
        head.setBcc(bcc);
    }

    /**
     * Adds an embedded jpeg image located at image_filename
     * @param image_filename
     */
    public void setEmbeddedJpeg(String image_filename) {
        setImage(image_filename, "embed");
    }

    /**
     * Attaches a jpeg image located at image_filename
     * @param image_filename
     * @throws IOException
     */
    public void setAttachedJpeg(String image_filename) {
        setImage(image_filename, "attach");
    }

    /**
     * Reads the image file image_filename into a buffered image that can be
     * marshalled into XML
     * @param image_filename
     * @param location
     */
    private void setImage(String image_filename, String location) {
        imgFile = new File(image_filename);
        BufferedImage bimg;
        try {
            bimg = ImageIO.read(imgFile);
            //Image img = bimg.getScaledInstance(512, -1, Image.SCALE_DEFAULT);
            image.setJPEGPicture(bimg);
            image.setLocation(location);
            content.setImage(image);
            et.setContent(content);
        } catch (IOException ex) {
            log.logp(Level.SEVERE, CNAME, "addImage", ex.toString());
        }
    }
    /**
     * Sends message to the default url
     */
    public void send(){
        send(defDstUrl);
    }
    /**
     * send method marshalls the jaxb object into an xml message and sends it to
     * the dstUrl (topic/room with e-mail consumer)
     * @param url
     */
    public void send(String url) {
        dstUrl = url;
        //Build XML string of Email
        JAXBElement<EmailType> email = of.createEmailMessage(et);

        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance(EmailType.class.getPackage().getName());
            marsh = jc.createMarshaller();
            sw = new StringWriter();
            marsh.marshal(email, sw);
            //System.out.println(sw.toString());
        } catch (JAXBException ex) {
            log.logp(Level.SEVERE, CNAME, "marshall", ex.toString());
        }

        pt.sendBody(dstUrl, sw.toString().getBytes());
    }
}
