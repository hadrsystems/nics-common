<?xml version="1.0" encoding="ISO-8859-1"?>
<!--

    (c) Copyright, 2008-2013 Massachusetts Institute of Technology.

        This material may be reproduced by or for the
        U.S. Government pursuant to the copyright license
        under the clause at DFARS 252.227-7013 (June, 1995).

-->
<StyledLayerDescriptor version="1.0.0"
    xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
    xmlns="http://www.opengis.net/sld"
    xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <NamedLayer>
        <Name>CollaborationRoomStyle</Name>
        <UserStyle>
            <Name>collabFeedStyle</Name>
            <Title/>
            <FeatureTypeStyle>
                <Name>name</Name>
                <Rule>
                    <Name>Label Style</Name>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>label</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <TextSymbolizer>
                        <Label>
                            <ogc:PropertyName>labeltext</ogc:PropertyName>
                        </Label>
                        <Font>
                            <CssParameter name="font-family">Free Sans</CssParameter>
                            <CssParameter name="font-size">
                                <ogc:PropertyName>labelsize</ogc:PropertyName>
                            </CssParameter>
                            <CssParameter name="font-style">Normal</CssParameter>
                            <CssParameter name="font-weight">normal</CssParameter>
                        </Font>
                        <LabelPlacement>
                            <PointPlacement>
                                <AnchorPoint>
                                    <AnchorPointX>
                                        <ogc:Literal>0.5</ogc:Literal>
                                    </AnchorPointX>
                                    <AnchorPointY>
                                        <ogc:Literal>0.5</ogc:Literal>
                                    </AnchorPointY>
                                </AnchorPoint>
                                <Rotation>
                                    <ogc:Literal>0.0</ogc:Literal>
                                </Rotation>
                            </PointPlacement>
                        </LabelPlacement>
                        <Fill>
                            <CssParameter name="fill">
                                <ogc:PropertyName>fillcolor</ogc:PropertyName>
                            </CssParameter>
                        </Fill>
                    </TextSymbolizer>
                </Rule>
            </FeatureTypeStyle>
            <FeatureTypeStyle>
                <Rule>
                    <Name>Solid Line Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsNull>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
            </ogc:PropertyIsNull>
          </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">
                                <ogc:PropertyName>strokecolor</ogc:PropertyName>
                            </CssParameter>
                            <CssParameter name="stroke-width">
                                <ogc:PropertyName>strokewidth</ogc:PropertyName>
                            </CssParameter>
              <CssParameter name="stroke-linecap">round</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                </Rule>
            </FeatureTypeStyle>
      <FeatureTypeStyle>
                <Rule>
                    <Name>Dash Line Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
              <ogc:Literal>dash</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">
                                <ogc:PropertyName>strokecolor</ogc:PropertyName>
                            </CssParameter>
                            <CssParameter name="stroke-width">
                                <ogc:PropertyName>strokewidth</ogc:PropertyName>
                            </CssParameter>
              <CssParameter name="stroke-dasharray">12 7</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                </Rule>
            </FeatureTypeStyle>
            <FeatureTypeStyle>
                <Rule>
                    <Name>polygon Style</Name>
                    <ogc:Filter>
                        <ogc:Or>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>polygon</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                      <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>circle</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>hexagon</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>triangle</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                         <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>box</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                        </ogc:Or>
                    </ogc:Filter>
                    <PolygonSymbolizer>
                        <Fill>
                            <CssParameter name="fill">
                                <ogc:PropertyName>fillcolor</ogc:PropertyName>
                            </CssParameter>
                            <CssParameter name="fill-opacity">
                                <ogc:PropertyName>opacity</ogc:PropertyName>
                            </CssParameter>
                        </Fill>
                        <Stroke>
                            <CssParameter name="stroke">
                                <ogc:PropertyName>strokecolor</ogc:PropertyName>
                            </CssParameter>
                            <CssParameter name="stroke-width">
                                <ogc:PropertyName>strokewidth</ogc:PropertyName>
                            </CssParameter>
                        </Stroke>
                    </PolygonSymbolizer>
                </Rule>
            </FeatureTypeStyle>
            <FeatureTypeStyle>
                <Rule>
                    <Name>icon</Name>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>marker</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <ExternalGraphic>
                                <OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="../nics/${graphic}"/>
                                <Format>image/png</Format>
                            </ExternalGraphic>
                            <Size>
                                <ogc:PropertyName>graphicheight</ogc:PropertyName>
                            </Size>
                            <Rotation>
                                <ogc:PropertyName>rotation</ogc:PropertyName>
                            </Rotation>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
            </FeatureTypeStyle>
      <FeatureTypeStyle>
                <Rule>
                    <Name>completedLine Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
              <ogc:Literal>completedLine</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#000000</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                </Rule>
            </FeatureTypeStyle>
      <FeatureTypeStyle>
                <Rule>
                    <Name>fireSpreadPrediction Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
              <ogc:Literal>fireSpreadPrediction</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#FF8000</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                </Rule>
            </FeatureTypeStyle>
      <FeatureTypeStyle>
                <Rule>
                    <Name>map Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
              <ogc:Literal>action-point</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
                    </ogc:Filter>
          <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#000000</CssParameter>
                            <CssParameter name="stroke-width">6</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#FF8000</CssParameter>
                            <CssParameter name="stroke-width">4</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
          <PointSymbolizer>
            <Geometry>
            <ogc:Function name="startPoint">
              <ogc:PropertyName>the_geom</ogc:PropertyName>
            </ogc:Function>
            </Geometry>
            <Graphic>
            <Mark>
              <WellKnownName>circle</WellKnownName>
              <Fill>
              <CssParameter name="fill">#FF8000</CssParameter>
              </Fill>
            </Mark>
            <Size>10</Size>
            </Graphic>
           </PointSymbolizer>
           <PointSymbolizer>
             <Geometry>
             <ogc:Function name="endPoint">
               <ogc:PropertyName>the_geom</ogc:PropertyName>
             </ogc:Function>
             </Geometry>
             <Graphic>
             <Mark>
               <WellKnownName>circle</WellKnownName>
               <Fill>
                <CssParameter name="fill">#FF8000</CssParameter>
               </Fill>
             </Mark>
             <Size>8</Size>
             </Graphic>
           </PointSymbolizer>
                </Rule>
            </FeatureTypeStyle>
      <FeatureTypeStyle>
                <Rule>
                    <Name>completedDozer Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
              <ogc:Literal>completed-dozer-line</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <GraphicStroke>
               <Graphic>
                 <Mark>
                 <WellKnownName>shape://times</WellKnownName>
                 <Stroke>
                   <CssParameter name="stroke">#000000</CssParameter>
                   <CssParameter name="stroke-width">2</CssParameter>
                 </Stroke>
                 </Mark>
                 <Size>10</Size>
               </Graphic>
               </GraphicStroke>
               <CssParameter name="stroke-dasharray">10 4</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                </Rule>
            </FeatureTypeStyle>
      <FeatureTypeStyle>
                <Rule>
                    <Name>proposedDozer Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
              <ogc:Literal>proposed-dozer-line</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <GraphicStroke>
               <Graphic>
                 <Mark>
                 <WellKnownName>shape://times</WellKnownName>
                 <Stroke>
                   <CssParameter name="stroke">#000000</CssParameter>
                   <CssParameter name="stroke-width">2</CssParameter>
                 </Stroke>
                 </Mark>
                 <Size>10</Size>
               </Graphic>
               </GraphicStroke>
               <CssParameter name="stroke-dasharray">10 18</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
          <LineSymbolizer>
           <Stroke>
             <CssParameter name="stroke">#000000</CssParameter>
             <CssParameter name="stroke-width">4</CssParameter>
             <CssParameter name="stroke-linecap">round</CssParameter>
             <CssParameter name="stroke-dasharray">1 27</CssParameter>
             <CssParameter name="stroke-dashoffset">6</CssParameter>
           </Stroke>
           </LineSymbolizer>
                </Rule>
            </FeatureTypeStyle>
      <FeatureTypeStyle>
                <Rule>
                    <Name>plannedFireline Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
              <ogc:Literal>primary-fire-line</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#000000</CssParameter>
                            <CssParameter name="stroke-width">6</CssParameter>
              <CssParameter name="stroke-dasharray">6 16</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                </Rule>
            </FeatureTypeStyle>
      <FeatureTypeStyle>
                <Rule>
                    <Name>secondaryFireline Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
              <ogc:Literal>secondary-fire-line</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <GraphicStroke>
               <Graphic>
                 <Mark>
                 <WellKnownName>circle</WellKnownName>
                 <Fill>
                   <CssParameter name="fill">#000000</CssParameter>
                 </Fill>
                 </Mark>
                 <Size>6</Size>
               </Graphic>
               </GraphicStroke>
               <CssParameter name="stroke-dasharray">6 16</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                </Rule>
            </FeatureTypeStyle>
      <FeatureTypeStyle>
                <Rule>
                    <Name>fireEdge Style</Name>
                    <ogc:Filter>
          <ogc:And>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>type</ogc:PropertyName>
                            <ogc:Literal>sketch</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>dashstyle</ogc:PropertyName>
              <ogc:Literal>fire-edge-line</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
                    </ogc:Filter>
          <LineSymbolizer>
           <Stroke>
             <GraphicStroke>
             <Graphic>
               <ExternalGraphic>
                 <OnlineResource xlink:type="simple" xlink:href="halfhatch.png" />
                 <Format>image/png</Format>
              </ExternalGraphic>
               <Size>16</Size>
             </Graphic>
             </GraphicStroke>
             <CssParameter name="stroke-dasharray">2 8</CssParameter>
           </Stroke>
           </LineSymbolizer>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#FF0000</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                </Rule>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>

