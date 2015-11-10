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
package edu.mit.ll.nics.common.geoserver.api;

import java.util.logging.Logger;


/**
 * API for interaction with geoserver REST interface
 * Layers and featuretypes can be added with a method
 * All types can be deleted by name
 * All types can be retrieved in xml or json
 * FeatureTypes and Layers can be added
 * TODO add more types
 * @author LE22005
 */
public class GeoWebCache extends SuperGeoServer {

    private static final Logger log = Logger.getLogger(GeoWebCache.class.getSimpleName());

    public GeoWebCache(String _url, String username, String password) {
        super(_url, username, password);
    }

    /**
     * Clear GeoWebCache's Cache for a specific layer
     * @param featuretypeName
     * @param srs spatial reference system number for the cache to be cleared
     * @param image format for the cache to be cleared
     * @return success of object removal
     */
    public boolean clearCache(String featureTypeName, int srs, String imageFormat, int threadCount) {
        int zoomStart = 0;
        int zoomStop = 20;
        String resetXML = "<seedRequest><name>" + featureTypeName
                + "</name><srs><number>" + srs
                + "</number></srs><zoomStart>" + zoomStart
                + "</zoomStart><zoomStop>" + zoomStop
                + "</zoomStop><format>" + imageFormat
                + "</format><type>truncate</type><threadCount>"
                + threadCount + "</threadCount></seedRequest>";
        return restPost("/seed/" + featureTypeName+".xml", resetXML, "text/xml");
    }
    
    /**
     * Seed GeoWebCache's cache for a specific layer
     * @param featuretypeName
     * @param zoomStart zoom level to start seeding at
     * @param zoomStop zoom level to stop seeding at
     * @param srs spatial reference system number for the cache to be cleared
     * @param image format for the cache to be cleared
     * @return success of object removal
     */
    public boolean seedCache(String featureTypeName, int zoomStart, int zoomStop, int srs, String imageFormat, int threadCount) {
        String seedXML = "<seedRequest><name>" + featureTypeName
                + "</name><srs><number>" + srs
                + "</number></srs><zoomStart>" + zoomStart
                + "</zoomStart><zoomStop>" + zoomStop
                + "</zoomStop><format>" + imageFormat
                + "</format><type>reseed</type><threadCount>"
                + threadCount + "</threadCount></seedRequest>";
        return restPost("/seed/" + featureTypeName+".xml", seedXML, "text/xml");
    }
    
    /**
     * Reload the geowebcache
     */
    public boolean reload(){
        return restPost("/reload","reload_configuration=1","text");
    }
}
