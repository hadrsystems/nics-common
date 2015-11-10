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
package edu.mit.ll.nics.common.messages.parser;

import edu.mit.ll.nics.common.messages.NICSMessage;
import edu.mit.ll.nics.common.messages.sadisplay.FeatureCommandType;
import edu.mit.ll.nics.common.messages.sadisplay.SADisplayMessageType;


public class SampleUsage {

	public static void main(String[] args) {
		String[] jsonMessages = {"{'feat':{'id':'OpenLayers.Feature.Vector_909rayd','from':{'user':'rayd','nick':'rayd'},'type':'draw','content':{'attrs':{'type':'sketch','created':'2010-05-28 13:47:57','eventname':'CARRUObispo','user':'rayd','opacity':0.4,'strokeWidth':'6','dashStyle':'solid','strokeColor':'#339966','fillColor':'#339966','pointRadius':2,'hasGraphic':false,'rotation':null},'geo':'LINESTRING(-13446037.643774 4209922.2062766,-13445961.206745 4209998.6433049,-13445808.332689 4210380.8284463,-13445579.021604 4210686.5765594,-13445349.710519 4210992.3246725,-13444661.777265 4211833.1319835,-13444279.592123 4212291.7541531,-13443744.532925 4212597.5022662,-13442980.162643 4212979.6874076,-13442062.918303 4213361.872549,-13440992.799908 4213667.6206621,-13440075.555568 4213896.9317469,-13438241.06689 4214049.8058035,-13437018.074437 4214202.67986,-13436559.452268 4214202.67986,-13436406.578211 4214126.2428317,-13436177.267126 4213973.3687752,-13436100.830098 4213896.9317469,-13436100.830098 4213591.1836338,-13436100.830098 4213132.5614642,-13436100.830098 4212673.9392945,-13436100.830098 4212138.8800966,-13436253.704155 4211527.3838704,-13436330.141183 4210763.0135876,-13436406.578211 4210075.0803332,-13436406.578211 4209387.1470787,-13436406.578211 4208622.776796,-13436406.578211 4208240.5916546,-13436406.578211 4207781.9694849,-13436406.578211 4207399.7843436,-13436330.141183 4206711.8510891,-13435565.7709 4205718.1697215,-13434648.526561 4204800.9253822,-13434189.904391 4204648.0513257,-13433578.408165 4204648.0513257,-13433272.660052 4204724.488354,-13432737.600854 4205106.6734953,-13432126.104628 4205565.295665,-13431591.04543 4206100.3548629,-13430979.549204 4206711.8510891,-13429985.867836 4207858.4065132,-13429297.934582 4208317.0286829,-13428533.564299 4208852.0878808,-13427387.008875 4209540.0211353,-13426546.201564 4209922.2062766,-13425170.335055 4210533.7025028,-13424711.712885 4210610.1395311,-13424329.527744 4210686.5765594,-13424253.090715 4210686.5765594,-13424176.653687 4210686.5765594,-13424100.216659 4210610.1395311,-13424100.216659 4210380.8284463,-13424100.216659 4210075.0803332,-13424329.527744 4208928.5249091,-13424635.275857 4207629.0954284,-13424788.149913 4206788.2881174,-13424864.586942 4206023.9178346,-13424864.586942 4205335.9845802,-13424941.02397 4204800.9253822,-13424941.02397 4204265.8661843,-13424941.02397 4203807.2440147,-13424941.02397 4203425.0588733,-13424941.02397 4203195.7477885,-13424941.02397 4203119.3107602,-13424864.586942 4203119.3107602,-13424864.586942 4203042.8737319,-13424788.149913 4203119.3107602,-13423335.846376 4203807.2440147,-13422800.787178 4204112.9921278,-13422112.853924 4204418.7402409,-13421501.357698 4204724.488354,-13421042.735528 4204800.9253822,-13420507.67633 4204953.7994388,-13419972.617132 4205030.2364671,-13419666.869019 4205030.2364671,-13419131.809821 4205030.2364671,-13418902.498736 4204877.3624105,-13418826.061708 4204724.488354,-13418826.061708 4204495.1772691,-13418673.187651 4204189.429156,-13418673.187651 4203654.3699581,-13418673.187651 4203119.3107602,-13418673.187651 4202584.2515623,-13418673.187651 4202125.6293926,-13418673.187651 4201514.1331664,-13418673.187651 4200902.6369402,-13418596.750623 4200138.2666575,-13418520.313595 4199603.2074596,-13418520.313595 4199221.0223182,-13418367.439538 4198838.8371768,-13418138.128453 4198609.526092,-13417908.817369 4198303.7779789,-13417679.506284 4198227.3409506,-13417450.195199 4198227.3409506,-13417220.884114 4198227.3409506,-13416915.136001 4198456.6520354,-13416685.824916 4198685.9631203,-13416380.076803 4198991.7112334,-13415997.891662 4199297.4593465,-13415615.70652 4199679.6444878,-13415080.647322 4199985.3926009,-13414774.899209 4200444.0147706,-13414469.151096 4200749.7628837,-13414239.840011 4201055.5109968)'}},'room':'CARRUObispo-InformationOfficer','time':'2010-05-28 13:47:57','ver':'1.0.3','ip':'127.0.0.1'}",
				"{'sys':{'type':'logout','params':300000,'msg':'system maintenance occurring soon. please log out.'},'time':'2010-16-03 17:29:05','user':'admin','ver':'1.0.1','ip':'127.0.0.1','incident':'CARRUSmith'}",
				"{'pres':{'type':'join','from':{'user':'rayd','nick':'rayd'}},'room':'CARRUObispo-InformationOfficer','time':'2010-05-28 13:47:55','ver':'1.0.3','ip':'127.0.0.1'}",
				"{'feat':{'id':'OpenLayers.Feature.Vector_909rayd','from':{'user':'rayd','nick':'rayd'},'type':'move','content':'-13428839.312412, 4212712.1578084'},'room':'CARRUObispo-InformationOfficer','time':'2010-05-28 15:50:0','ver':'1.0.3','ip':'127.0.0.1'}",
				"{'feat':{'id':'OpenLayers.Feature.Vector_928rayd','from':{'user':'rayd','nick':'rayd'},'type':'modify','content':{'rotation':235.46}},'room':'CARRUObispo-InformationOfficer','time':'2010-05-28 15:50:43','ver':'1.0.3','ip':'127.0.0.1'}",
				"{'feat':{'id':'OpenLayers.Feature.Vector_928rayd','from':{'user':'rayd','nick':'rayd'},'type':'remove','content':null},'room':'CARRUObispo-InformationOfficer','time':'2010-05-28 15:52:39','ver':'1.0.3','ip':'127.0.0.1'}",
				"{'sys':{'type':'setting','params':'test','msg':'here\\'s a message'},'time':'2010-05-28 15:53:21','user':'rayd','ver':'1.0.3','ip':'127.0.0.1','incident':'CARRUObispo'}",
				"{'map':{'from':{'user':'rayd','nick':'rayd'},'bounds':'-13479555.28067,4173882.147446,-13377358.973872,4230521.985396','proj':'EPSG:3857'},'room':'CARRUObispo-InformationOfficer','time':'2010-05-28 15:55:58','ver':'1.0.3','ip':'127.0.0.1'}",
				"{'msg':{'id':'rayd-1275062203187','from':{'user':'rayd','nick':'rayd'},'body':'this is a \\'test\\' message'},'room':'CARRUObispo-InformationOfficer','time':'2010-05-28 15:56:43','ver':'1.0.3','ip':'127.0.0.1'}",
				"{'stat':{'repType':'new','incident':'Incident88','incNum':'88','stTime':'Today','loc':'NW of Perris','incType':'brush','cause':'cigarette','size':'114','ros':'2','fuel':'brush,grass','pot':'large','contained':15,'estContain':'a','estControl':'b','curWeather':'windy','preWeather':'windy','evac':'none','struct':'none','infrastruct':'none','icpLoc':'main st','tankers':0,'helicopters':1,'overhead':7,'t1engines':2,'t2engines':1,'t3engines':0,'watertender':3,'dozers':2,'crews':5,'comunit':5,'genCom':'going well','reportby':'rayd','reporttm':'2010-06-18 14:50:47'},'user':'rayd','time':'2010-06-18 14:50:47','ver':'1.0.4','ip':'127.0.0.1','incident':'Incident88'}",
				"{'res':'this is a resource message!','user':'rayd','time':'2010-05-28 16:0:46','ver':'1.0.3','ip':'127.0.0.1','incident':'CARRUObispo'}",
				"{'pres':{'type':'exit','from':{'user':'rayd','nick':'rayd'}},'room':'CARRUObispo-InformationOfficer','time':'2010-05-28 16:4:33','ver':'1.0.3','ip':'127.0.0.1'}"
			};

		for (String string : jsonMessages) {
			NICSMessage message = SADisplayMessageParser.parse(string, true);
			if(message == null){
				return;
			}
			
			//System.out.println("[" + message.getMessageType() + "] " + message + "\n");
			System.out.println(message.getValue("messageType"));
			
			/*IncidentStatusMessage incStatMsg = (IncidentStatusMessage)message;
			
			System.out.println(incStatMsg.getIncident());
			
			if(message.getMessageType() == SADisplayMessageType.FEATURE && ((FeatureMessage)message).getCommandType() == FeatureCommandType.ADD){
				System.out.println("[JSON from Object] " + ((FeatureAddMessage)message).toJSONString() + "\n");
			}*/
		}
		
		
	}

}
