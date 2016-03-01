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

import edu.mit.ll.nics.common.email.constants.JsonEmailConstants;
import edu.mit.ll.nics.common.email.exception.JsonEmailException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ch23050 on 1/9/16.
 */
public class JsonEmail
{
    private String from;
    private String to;
    private String subject;
    private String body = null;

    /**
     *
     * @param from - Sender's email address
     * @param to - Email recipient(s). Multiple recipients delimitted with a comma (',')
     * @param subject - Subject title
     */
    public JsonEmail(final String from, final String to, final String subject)
    {
        this.from = from;
        this.to = to;
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public JSONObject toJsonObject() throws JsonEmailException
    {
        try
        {
            JSONObject json = new JSONObject();
            json.put(JsonEmailConstants.FROM, from);
            json.put(JsonEmailConstants.TO, to);
            json.put(JsonEmailConstants.SUBJECT, subject);
            if (body == null)
                body = "";
            json.put(JsonEmailConstants.BODY, body);

            return json;
        } catch (JSONException je)
        {
            throw new JsonEmailException("Unhandled JSONException building JsonEmail JSON", je);
        }
    }

    public static JsonEmail fromJSONString(String jsonStr)
            throws JsonEmailException
    {
        try
        {
            JSONObject json = new JSONObject(jsonStr);
            return JsonEmail.fromJSONObject(json);
        } catch (JSONException je)
        {
            throw new JsonEmailException("Invalid JSON", je);
        }
    }

    public static JsonEmail fromJSONObject(JSONObject json)
            throws JsonEmailException
    {

        if (json.has(JsonEmailConstants.TO) &&
                json.has(JsonEmailConstants.FROM) &&
                json.has(JsonEmailConstants.SUBJECT) &&
                json.has(JsonEmailConstants.BODY))
        {
            try
            {
                JsonEmail je = new JsonEmail(json.getString(JsonEmailConstants.FROM),
                        json.getString(JsonEmailConstants.TO),
                        json.getString(JsonEmailConstants.SUBJECT));
                je.setBody(json.getString(JsonEmailConstants.BODY));

                return je;
            }catch (JSONException je)
            {
                throw new JsonEmailException("Unhandled JSONException", je);
            }
        } else
        {
            throw new JsonEmailException("Invalid JSON properties");
        }
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }
}
