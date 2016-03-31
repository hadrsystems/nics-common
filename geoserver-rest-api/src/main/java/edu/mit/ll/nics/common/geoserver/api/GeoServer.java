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
package edu.mit.ll.nics.common.geoserver.api;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.vividsolutions.jts.geom.Envelope;
import java.util.HashMap;

/**
 * API for interaction with geoserver REST interface
 * Layers and featuretypes can be added with a method
 * All types can be deleted by name
 * All types can be retrieved in xml or json
 * FeatureTypes and Layers can be added
 * TODO add more types
 * @author LE22005
 */
public class GeoServer extends SuperGeoServer {

    private static final Logger log = Logger.getLogger(GeoServer.class.getSimpleName());

    public GeoServer(String _url, String username, String password) {
        super(_url, username, password);
    }

    /**
     * @return Return all the layers in geoserver
     */
    public List<String> getLayerList() {
        return getList("/layers.json", "layer");
    }

    /**
     * Get layer 
     * @param layerName
     * @param contentType
     * @return string of layer object in contentType
     */
    public String getLayer(String layerName, String contentType) {
        return restGet("/layers/" + layerName, contentType);
    }

    /**
     * Update layer's style using following paramaters
     * @param layerName
     * @param style
     * @return success of updating object
     */
    public boolean updateLayerStyle(String layerName, String style) {
        String xml = "<layer>"
                //+ "<name>" + layerName + "</name>"
                + "<defaultStyle>"
                + "<name>" + style + "</name>"
                + "</defaultStyle>"
                + "<enabled>true</enabled>"
                + "</layer>";
        return restPut("/layers/" + layerName, xml, "application/xml");
    }

    /**
     * Set whether a layer is enabled
     * @param layerName
     * @param enabled boolean
     * @return success of updating object
     */
    public boolean updateLayerEnabled(String layerName, boolean enabled) {
        String xml = "<layer>"
                //+ "<name>" + layerName + "</name>"
                + "<enabled>" + enabled + "</enabled>"
                + "</layer>";
        return restPut("/layers/" + layerName, xml, "application/xml");
    }

    /**
     * Get layer 
     * @param layerName
     * @param workspaceName
     * @param contentType
     * @return string of layer object in contentType
     */
    public String getLayer(String layerName, String workspaceName, String contentType) {
        return restGet("/layers/" + workspaceName + ":" + layerName, contentType);
    }

    /**
     * Update layer's style using following paramaters
     * @param layerName
     * @param workspaceName
     * @param style
     * @return success of updating object
     */
    public boolean updateLayerStyle(String layerName, String workspaceName, String style) {
        String xml = "<layer>"
                //+ "<name>" + layerName + "</name>"
                + "<defaultStyle>"
                + "<name>" + style + "</name>"
                + "</defaultStyle>"
                + "<enabled>true</enabled>"
                + "</layer>";
        return restPut("/layers/" + workspaceName + ":" + layerName + ".xml", xml, "application/xml");
    }

    /**
     * Set whether a layer is enabled
     * @param layerName
     * @param workspaceName
     * @param enabled boolean
     * @return success of updating object
     */
    public boolean updateLayerEnabled(String layerName, String workspaceName, boolean enabled) {
        String xml = "<layer>"
                //+ "<name>" + layerName + "</name>"
                + "<enabled>" + enabled + "</enabled>"
                + "</layer>";
        return restPut("/layers/" + workspaceName + ":" + layerName + ".xml", xml, "application/xml");
    }
    
    /**
     * Remove layer
     * @param layerName
     * @return success of removal
     */
    public boolean removeLayer(String layerName) {
        return restDelete("/layers/" + layerName);
    }

    /**
     * @return Return all the layerGroups in geoserver
     */
    public List<String> getLayerGroupList() {
        return getList("/layerGroups.json", "layerGroup");
    }

    /**
     * Gets layerGroup with layerGroupname
     * @param layerGroupName
     * @param contentType
     * @return string of layer group object in specified format
     */
    public String getLayerGroup(String layerGroupName, String contentType) {
        return restGet("/layerGroups/" + layerGroupName, contentType);
    }

    /**
     * Add layerGroup using following paramaters
     * @param layerGroupName
     * @param layerGroupTitle
     * @param style
     * @param layerList layer names mapped to styles
     * @return success of adding object
     */
    public boolean addLayerGroup(String layerGroupName, String layerGroupTitle, String style, Map<String, String> layerList) {
        try {
            JSONObject layerGroup = new JSONObject();
            layerGroup.put("name", layerGroupName);
            JSONArray layers = new JSONArray();
            JSONArray styles = new JSONArray();
            for (String layerName : layerList.keySet()) {
                layers.put(new JSONObject().put("name", layerName));
                styles.put(new JSONObject().put("name", layerList.get(layerName)));
            }
            layerGroup.put("layers", new JSONObject().put("layer", layers));
            layerGroup.put("styles", new JSONObject().put("style", styles));
            JSONObject json = new JSONObject().put("layerGroup", layerGroup);
            return restPost("/layerGroups", json.toString(), "application/json");
        } catch (JSONException ex) {
            Logger.getLogger(GeoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Remove layer group
     * @param layerGroupName
     * @return success of object removal
     */
    public boolean removeLayerGroup(String layerGroupName) {
        return restDelete("/layerGroups/" + layerGroupName);
    }

    /**
     * @return all of the workspace names in geoserver
     */
    public List<String> getWorkspaceList() {
        return getList("/workspaces.json", "workspace");
    }

    /**
     * Get workspace object in specified format
     * @param workspaceName
     * @param contentType
     * @return string of workspace in contentType
     */
    public String getWorkspace(String workspaceName, String contentType) {
        return restGet("/workspaces/" + workspaceName, contentType);
    }

    /**
     * Remove workspace
     * @param workspaceName
     * @return success of object removal
     */
    public boolean removeWorkspace(String workspaceName) {
        return restDelete("/workspaces/" + workspaceName);
    }

    /**
     * @return all of the dataStore names in geoserver
     */
    public List<String> getDataStoreList(String workspaceName) {
        return getList("/workspaces/" + workspaceName + "/datastores.json", "dataStore");
    }

    /**
     * Get dataStore object in specified format
     * @param dataStoreName
     * @param contentType
     * @return dataStore object of contentType in string format
     */
    public String getDataStore(String workspaceName, String dataStoreName, String contentType) {
        return restGet("/workspaces/" + workspaceName + "/datastores/" + dataStoreName, contentType);
    }

    /**
     * Remove data store
     * @param workspaceName
     * @param dataStoreName
     * @return success of object removal
     */
    public boolean removeDataStore(String workspaceName, String dataStoreName) {
        return restDelete("/workspaces/" + workspaceName + "/datastores/" + dataStoreName);
    }

    /**
     * @return all of the featureType names in geoserver
     */
    public List<String> getFeatureTypeList(String workspaceName, String dataStoreName) {
        return getList("/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes.json", "featureType");
    }

    /**
     * Get featureType string object with specified contentType
     * @param featureTypeName
     * @param contentType
     * @return string of featureType object
     */
    public String getFeatureType(String workspaceName, String dataStoreName, String featureTypeName, String contentType) {
        return restGet("/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes/" + featureTypeName + ".json", contentType);
    }
    
    /**
     * Update a feature type title
     * @param layerName
     * @param workspaceName
     * @param title
     * @return success of updating object
     */
    public boolean updateFeatureTypeTitle(String featureTypeName, String workspaceName, String dataStoreName, String title) {
        String xml = "<featureType>"
                + "<title>" + title + "</title>"
                + "</featureType>";
        return restPut("/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes/" + featureTypeName, xml, "application/xml");
    }

    /**
     * Private function to build XML from feature type paramaters
     * @param workspaceName
     * @param dataStoreName
     * @param featureTypeName
     * @param srs
     * @param sqlFilter
     * @param geometryAttribute
     * @param geometryType
     * @param srid
     * @return 
     */
    private String buildFeatureTypeXML(String workspaceName, String dataStoreName, String featureTypeName, String srs,
            String sqlFilter, String geometryAttribute, String geometryType, Integer srid) {
        String xml = "<featureType>"
                + "<name>" + featureTypeName + "</name>"
                + "<nativeName>" + featureTypeName + "</nativeName>"
                + "<namespace>"
                + "<name>" + workspaceName + "</name>"
                + "</namespace>"
                + "<title>" + featureTypeName + "</title>"
                + "<srs>" + srs + "</srs>"
                + "<enabled>true</enabled>";
        if (sqlFilter != null) {
            xml = xml + "<metadata>"
                    + "<entry key=\"JDBC_VIRTUAL_TABLE\">"
                    + "<virtualTable>"
                    + "<name>" + featureTypeName + "</name>"
                    + "<sql>" + sqlFilter + "</sql>"
                    + "<geometry>"
                    + "<name>" + geometryAttribute + "</name>"
                    + "<type>" + geometryType + "</type>"
                    + "<srid>" + srid.toString() + "</srid>"
                    + "</geometry>"
                    + "</virtualTable>"
                    + "</entry>"
                    + "</metadata>";
        }
        xml = xml + "<store class=\"dataStore\">"
                + "<name>" + dataStoreName + "</name>"
                + "</store>"
                + "<maxFeatures>0</maxFeatures>"
                + "<numDecimals>0</numDecimals>"
                + "</featureType>";
        return xml;
    }

    /**
     * Add a simple Feature type to geoserver
     * @param workspaceName
     * @param dataStoreName
     * @param featureTypeName name of table to link featuretype to (if postgis)
     * @param srs
     * @return success of object add
     */
    public boolean addFeatureType(String workspaceName, String dataStoreName, String featureTypeName, String srs) {
        String xml = buildFeatureTypeXML(workspaceName, dataStoreName, featureTypeName, srs, null, null, null, null);
        return restPost("/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes", xml, "application/xml");
    }

    /**
     * Add a SQL view Feature type to geoserver 
     * @param workspaceName
     * @param dataStoreName
     * @param featureTypeName
     * @param srs
     * @param sqlFilter sql query to build view with
     * @return success of object add
     */
    public boolean addFeatureTypeSQL(String workspaceName, String dataStoreName, String featureTypeName, String srs, String sqlFilter, String geometryAttribute, String geometryType, Integer srid) {
        String xml = buildFeatureTypeXML(workspaceName, dataStoreName, featureTypeName, srs, sqlFilter, geometryAttribute, geometryType, srid);
        return restPost("/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes", xml, "application/xml");
    }

    /**
     * Update the native bounds for a feature type
     * @param workspaceName
     * @param dataStoreName
     * @param featureTypeName
     * @param bounds
     * @param srs of the bounds
     * @return success of update
     */
    public boolean updateFeatureTypeNativeBounds(String workspaceName, String dataStoreName, String featureTypeName, Envelope bounds, String srs) {
        //If envelope isn't an envelope (doesn't have 4 points)
        if (bounds.isNull()) {
            log.warning("Incorrect bounds envelope must have 4 points");
            return false;
        }
        String xml = "<featureType>"
                /*+ "<name>" + featureTypeName + "</name>"
                + "<nativeName>" + featureTypeName + "</nativeName>"
                + "<namespace>"
                + "<name>" + workspaceName + "</name>"
                + "</namespace>"
                + "<title>" + featureTypeName + "</title>"*/
                + "<nativeBoundingBox>"
                + "<minx>" + bounds.getMinX() + "</minx>"
                + "<miny>" + bounds.getMinY() + "</miny>"
                + "<maxx>" + bounds.getMaxX() + "</maxx>"
                + "<maxy>" + bounds.getMaxY() + "</maxy>"
                + "<crs class=\"projected\">" + srs + "</crs>"
                + "</nativeBoundingBox>"
                + "</featureType>";
        return restPut("/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes/" + featureTypeName, xml, "application/xml");
    }

    /**
     * Update the native and latlon bounds for a feature type
     * @param workspaceName
     * @param dataStoreName
     * @param featureTypeName
     * @param nativeBounds
     * @param latlonBounds
     * @param srs of the bounds
     * @return success of update
     */
    public boolean updateFeatureTypeBounds(String workspaceName, String dataStoreName, String featureTypeName, Envelope nativeBounds, Envelope latlonBounds, String srs) {
        //If envelope isn't an envelope (doesn't have 4 points)
        if (latlonBounds.isNull() || nativeBounds.isNull()) {
            log.warning("Incorrect bounds envelope must have 4 points");
            return false;
        }
        String xml = "<featureType>"
                /*+ "<name>" + featureTypeName + "</name>"
                + "<nativeName>" + featureTypeName + "</nativeName>"
                + "<namespace>"
                + "<name>" + workspaceName + "</name>"
                + "</namespace>"
                + "<title>" + featureTypeName + "</title>"*/
                + "<nativeBoundingBox>"
                + "<minx>" + nativeBounds.getMinX() + "</minx>"
                + "<miny>" + nativeBounds.getMinY() + "</miny>"
                + "<maxx>" + nativeBounds.getMaxX() + "</maxx>"
                + "<maxy>" + nativeBounds.getMaxY() + "</maxy>"
                + "<crs class=\"projected\">" + srs + "</crs>"
                + "</nativeBoundingBox>"
                + "<latLonBoundingBox>"
                + "<minx>" + latlonBounds.getMinX() + "</minx>"
                + "<miny>" + latlonBounds.getMinY() + "</miny>"
                + "<maxx>" + latlonBounds.getMaxX() + "</maxx>"
                + "<maxy>" + latlonBounds.getMaxY() + "</maxy>"
                + "<crs>EPSG:4326</crs>"
                + "</latLonBoundingBox>"
                + "</featureType>";
        return restPut("/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes/" + featureTypeName, xml, "application/xml");
    }

    /**
     * Enable or disable a featuretype
     * @param workspaceName
     * @param dataStoreName
     * @param featureTypeName
     * @param enabled
     * @return success of update
     */
    public boolean updateFeatureTypeEnabled(String workspaceName, String dataStoreName, String featureTypeName, boolean enabled) {
        String xml = "<featureType>"
                + "<enabled>" + enabled + "</enabled>"
                + "</featureType>";
        return restPut("/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes/" + featureTypeName, xml, "application/xml");
    }

    /**
     * Remove featureType
     * @param workspaceName
     * @param dataStoreName
     * @param featureTypeName
     * @return success of feature type removal
     */
    public boolean removeFeatureType(String workspaceName, String dataStoreName, String featureTypeName) {
        return restDelete("/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes/" + featureTypeName);
    }

    /**
     * @return Return all the Styles in geoserver
     */
    public List<String> getStyles() {
        return getList("/styles.json", "styles");
    }

    /**
     * Gets the style sheet for s
     * @param styleName
     * @return string of the sld
     */
    public String getStyle(String styleName) {
        String contentType = "application/vnd.ogc.sld+xml";
        return restGet("/styles/" + styleName, contentType);
    }

    /**
     * Add Style from the following paramaters
     * @param styleName
     * @param styleXml
     * @return success of adding object
     */
    public boolean addStyle(String styleName, String styleXml) {
        return restPost("/styles?name=" + styleName, styleXml, "application/vnd.ogc.sld+xml");
    }

    /**
     * Update Style from the following paramaters
     * @param styleName
     * @param styleXml
     * @return success of udpating object
     */
    public boolean updateStyle(String styleName, String styleXml) {
        return restPut("/styles/" + styleName, styleXml, "application/vnd.ogc.sld+xml");
    }

    /**
     * Remove Style
     * @param syleName
     * @return success of object removal
     */
    public boolean removeStyle(String styleName) {
        return restDelete("/styles/" + styleName);
    }

    /**
     * Clear Cache
     * @param featuretypeName
     * @param srs spatial reference system number for the cache to be cleared
     * @param image format for the cache to be cleared
     * @return success of object removal
     */
    public boolean clearCache(String featureTypeName, int srs, String imageFormat) {
        int zoomStart = 0;
        int zoomStop = 20;
        int threadCount = 4;
        String resetXML = "<seedRequest><name>" + featureTypeName
                + "</name><srs><number>" + srs
                + "</number></srs><zoomStart>" + zoomStart
                + "</zoomStart><zoomStop>" + zoomStop
                + "</zoomStop><format>" + imageFormat
                + "</format><type>truncate</type><threadCount>"
                + threadCount + "</threadCount></seedRequest>";
        return restPost("/gwc/rest/seed/" + featureTypeName, resetXML, "text/xml");
    }
}
