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
package edu.mit.ll.nics.common.rabbitmq.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.otp.erlang.OtpAuthException;
import com.ericsson.otp.erlang.OtpConnection;
import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangBinary;
import com.ericsson.otp.erlang.OtpErlangExit;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpPeer;
import com.ericsson.otp.erlang.OtpSelf;

/*
 add_user        <UserName> <Password>
 delete_user     <UserName>
 change_password <UserName> <NewPassword>
 list_users

 add_vhost    <VHostPath>
 delete_vhost <VHostPath>
 list_vhosts

 set_permissions   [-p <VHostPath>] <UserName> <Regexp> <Regexp> <Regexp>
 clear_permissions [-p <VHostPath>] <UserName>
 list_permissions  [-p <VHostPath>]
 list_user_permissions <UserName>

 list_queues    [-p <VHostPath>] [<QueueInfoItem> ...]
 list_exchanges [-p <VHostPath>] [<ExchangeInfoItem> ...]
 list_bindings  [-p <VHostPath>]
 list_connections [<ConnectionInfoItem> ...]

 */

public class RabbitAdmin {

    private Logger log;
    private OtpSelf self;
    private OtpConnection conn;
    private OtpPeer peer;

    private int connectTries;

    public RabbitAdmin(String cookie, String peerNode) {

        log = LoggerFactory.getLogger(RabbitAdmin.class);

        try {
            self = new OtpSelf("guest", cookie);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        peer = new OtpPeer(peerNode);
        peer.setCookie(cookie);

        connectTries = 0;
    }

    private void connect() {
        connectTries++;

        // try connecting a few times, have noticed there are occasional
        // problems
        if (connectTries <= 5) {
            log.debug("Connecting...");
            // Be sure to close the connection!
            try {
                conn = self.connect(peer);
                if (conn.isConnected()) {
                    log.debug("\tconnected!");
                    connectTries = 0;
                }
            } catch (Exception e) {
                log.error("error connecting!!", e);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    log.error("error sleeping", e1);
                } finally {
                    connect();
                }
            }
        }
    }

    private void disconnect() {
        log.debug("Disconnecting...");
        conn.close();
        log.debug("\tdisconnected.");
        conn = null;
        connectTries = 0;
    }

    public List<String> list_users() {

        log.info("listing users");

        OtpErlangObject result = null;
        List<String> userList = new ArrayList<String>();
        this.connect();
        try {
            conn.sendRPC("rabbit_auth_backend_internal", "list_users",
                    new OtpErlangList());
            result = conn.receiveRPC();
            log.debug("Received result: " + result);

            OtpErlangList test = (OtpErlangList) result;

            log.debug("Result list: ");
            String list = "";
            String user = "";

            Iterator<OtpErlangObject> iter = test.iterator();
            while (iter.hasNext()) {

                OtpErlangBinary bin = (OtpErlangBinary) iter.next();
                user = new String(bin.binaryValue());
                list += user + "\n";
                userList.add(user);
                // log.debug("test: " + new String(bin.binaryValue()));

            }
            log.debug(list);

        } catch (OtpErlangExit e) {
            log.debug("Erlang exited while receiving result");
            log.error(e.getMessage());
        } catch (OtpAuthException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        this.disconnect();
        return userList;
    }

    public boolean add_user(String username, String password) {

        log.info("adding user: {}", username);

        OtpErlangObject result = null;
        this.connect();
        boolean retval = false;

        OtpErlangBinary nameBytes = new OtpErlangBinary(username.getBytes());
        OtpErlangBinary passBytes = new OtpErlangBinary(password.getBytes());

        try {
            OtpErlangBinary[] args = new OtpErlangBinary[2];
            args[0] = nameBytes;
            args[1] = passBytes;
            conn.sendRPC("rabbit_auth_backend_internal", "add_user",
                    new OtpErlangList(args));

            result = conn.receiveRPC();
            log.debug("Received result: " + result);

            if (result instanceof OtpErlangAtom) {
                OtpErlangAtom res = (OtpErlangAtom) result;
                if (res.atomValue().equalsIgnoreCase("ok")) {
                    retval = true;
                } else {
                    log.error(res.toString());
                    retval = false;
                }
            } else {
                OtpErlangTuple res = (OtpErlangTuple) result;
                OtpErlangAtom atom = (OtpErlangAtom) res.elementAt(0);
                if (atom.atomValue().equalsIgnoreCase("error")) {
                    log.error(atom.toString());
                    retval = false;
                }
            }

            log.debug("result: " + retval);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        this.disconnect();
        return retval;
    }

    public boolean delete_user(String username) {

        log.info("deleting user: {}", username);

        OtpErlangObject result = null;
        this.connect();
        boolean retval = false;

        OtpErlangBinary nameBytes = new OtpErlangBinary(username.getBytes());

        try {

            conn.sendRPC("rabbit_auth_backend_internal", "delete_user",
                    new OtpErlangList(nameBytes));

            result = conn.receiveRPC();
            log.debug("Received result: " + result);

            if (result instanceof OtpErlangAtom) {
                OtpErlangAtom res = (OtpErlangAtom) result;
                if (res.atomValue().equalsIgnoreCase("ok")) {
                    retval = true;
                } else {
                    retval = false;
                }
            } else {
                OtpErlangTuple res = (OtpErlangTuple) result;
                OtpErlangAtom atom = (OtpErlangAtom) res.elementAt(0);
                if (atom.atomValue().equalsIgnoreCase("error")) {
                    retval = false;
                }
            }

            log.debug("result: " + retval);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        this.disconnect();
        return retval;
    }

    public boolean set_permissions(String username, String config,
            String write, String read) {
        log.debug("setting permissions for: " + username);

        OtpErlangObject result = null;
        this.connect();
        boolean retval = false;

        OtpErlangBinary nameBytes = new OtpErlangBinary(username.getBytes());
        OtpErlangBinary configBytes = new OtpErlangBinary(config.getBytes());
        OtpErlangBinary writeBytes = new OtpErlangBinary(write.getBytes());
        OtpErlangBinary readBytes = new OtpErlangBinary(read.getBytes());
        OtpErlangBinary vhostBytes = new OtpErlangBinary("/".getBytes());

        try {
            OtpErlangBinary[] args = new OtpErlangBinary[5];
            args[0] = nameBytes;
            args[1] = vhostBytes;
            args[2] = configBytes;
            args[3] = writeBytes;
            args[4] = readBytes;
            conn.sendRPC("rabbit_auth_backend_internal", "set_permissions",
                    new OtpErlangList(args));
            log.debug("about to send");
            result = conn.receiveRPC();
            log.debug("Received result: " + result);

            if (result instanceof OtpErlangAtom) {
                OtpErlangAtom res = (OtpErlangAtom) result;
                if (res.atomValue().equalsIgnoreCase("ok")) {
                    retval = true;
                } else {
                    retval = false;
                }
            } else {
                OtpErlangTuple res = (OtpErlangTuple) result;
                OtpErlangAtom atom = (OtpErlangAtom) res.elementAt(0);
                if (atom.atomValue().equalsIgnoreCase("error")) {
                    retval = false;
                }
            }

            log.debug("result: " + retval);

        } catch (Exception e) {
            log.debug("exception: " + e);
        }

        this.disconnect();
        return retval;
    }

    /**
     * change_password - change the Rabbit User password
     * 
     * @param username
     * @param password
     * @return indicates whether the change was successful
     */
    public boolean change_password(String username, String password) {

        log.info("changing password for: {}", username);

        OtpErlangObject result = null;
        this.connect();
        boolean retval = false;

        OtpErlangBinary nameBytes = new OtpErlangBinary(username.getBytes());
        OtpErlangBinary passBytes = new OtpErlangBinary(password.getBytes());

        try {
            OtpErlangBinary[] args = new OtpErlangBinary[2];
            args[0] = nameBytes;
            args[1] = passBytes;
            conn.sendRPC("rabbit_auth_backend_internal", "change_password",
                    new OtpErlangList(args));

            result = conn.receiveRPC();
            log.debug("Received result: " + result);

            if (result instanceof OtpErlangAtom) {
                OtpErlangAtom res = (OtpErlangAtom) result;
                if (res.atomValue().equalsIgnoreCase("ok")) {
                    retval = true;
                } else {
                    retval = false;
                }
            } else {
                OtpErlangTuple res = (OtpErlangTuple) result;
                OtpErlangAtom atom = (OtpErlangAtom) res.elementAt(0);
                if (atom.atomValue().equalsIgnoreCase("error")) {
                    retval = false;
                }
            }

            log.debug("result: " + retval);

        } catch (Exception e) {
            log.info(e.getMessage());
            retval = false;
        }

        this.disconnect();
        return retval;
    }

}
