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
/**
 * 
 */
package edu.mit.ll.nics.common.security.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class PasswordEncrypter {

    private final String ENC_PASS = "LdDrSKey";
    private final String ALGORITHM = "PBEWithSHA1andDESede";
    private final String SALT = "lDdr$@1t";
    private final int ITER_COUNT = 37;
    private Cipher pbeCipherEnc = null;
    private Cipher pbeCipherDec = null;
    
    private Logger log;
    
    /**
     * 
     */
    public PasswordEncrypter() {
        
        log = LoggerFactory.getLogger(PasswordEncrypter.class);
        
        byte[] salt = SALT.getBytes();
        
        // Create PBE parameter set
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, ITER_COUNT);
            
            char[] passArray = ENC_PASS.toCharArray();
            PBEKeySpec keySpec = new PBEKeySpec(passArray);
            SecretKey key = null;
            try {
                    // create key
                    key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec);
                    // create ciphers
                    pbeCipherEnc = Cipher.getInstance(ALGORITHM);
                    pbeCipherDec = Cipher.getInstance(ALGORITHM);
                    pbeCipherEnc.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);      
                    pbeCipherDec.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);
                    
            } catch (Exception e) {
                    log.error("Error initializing password encrypter: {}", e);
            }
            log.debug("RabbitAdmin security intialized");
    }
    
    // security methods
    public byte[] encrypt(String cleartext){
            byte[] ciphertext = null;
            try {
                    ciphertext = this.pbeCipherEnc.doFinal(cleartext.getBytes());
            } catch (Exception e) {
                    log.error("Error encrypting! {}", e);
            }
            //log.debug("returning encrypted text: {}" , ciphertext);
            return ciphertext;              
    }
    
    public String decrypt(byte[] ciphertext){
            String cleartext = null;
            try {
                    cleartext = new String(this.pbeCipherDec.doFinal(ciphertext));
            } catch (Exception e) {
                    log.error("Error decrypting! {}", e);
            }
            //log.debug("returning decrypted text: {}", cleartext);
            return cleartext;               
    }
}
