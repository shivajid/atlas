/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hortonworks.atlas.client;

import java.util.List;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.apache.atlas.typesystem.persistence.Id;
import org.apache.atlas.typesystem.types.DataTypes;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.collect.ImmutableList;

public class AtlasEntityConnector {


	{
		System.setProperty("atlas.conf", "./conf");
	}
	
	private  AtlasClient ac = null;
	
	
	public static void main(String[] args) throws Exception {
	
		if(args.length < 0)
			throw new Exception("Please pass the atlas base url");
		
		String baseurl = args[0];
		String inp_type_name = args[1];
		String inp_value = args[2];
		
		String out_type_name = args[3];
		String out_value = args[4];
		
		System.out.println(baseurl);
		System.out.println(inp_type_name);
		System.out.println(inp_value);
		
		
		System.out.println(" Baseurl" + baseurl);
		AtlasEntitySearch aES = new AtlasEntitySearch(baseurl);
		AtlasEntityConnector aec = new AtlasEntityConnector();
		aec.ac = aES.getAtlasClient();
		
		try {
			
			Referenceable inpref = aES.getReferenceByName(inp_type_name, inp_value);
			
			Referenceable outref = aES.getReferenceByName(out_type_name, out_value);
		
			Referenceable proc = aec.loadProcess(AtlasTypeDefCreator.Type_New_Life, "AsteroidConnector", "This  connects 2 Asteroids", ImmutableList.of(inpref.getId()), ImmutableList.of(outref.getId()), "Red");
			
			aec.createEntity(proc);
			
			System.out.println(" The 2 objects are connected");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	 /*
	  * 
	  * 
	  */
	public Referenceable loadProcess(String type, String name, String description, List<Id> inputTables, List<Id> outputTables,
           String... traitNames)
   throws Exception {
       Referenceable referenceable = new Referenceable(type, traitNames);
       // super type attributes
       referenceable.set("name", name);
       referenceable.set("description", description);
       referenceable.set("inputs", inputTables);
       referenceable.set("outputs", outputTables);
       referenceable.set("userName", "sdutta");
       referenceable.set("startTime", System.currentTimeMillis());
       referenceable.set("endTime", System.currentTimeMillis());	
       
       return referenceable;

	}
	
	
	/**
	 * 
	 * This is a generic method of creating entities of any class
	 * @throws JSONException 
	 * @throws AtlasServiceException 
	 * 
	 */
	public Id createEntity(Referenceable ref ) throws JSONException, AtlasServiceException{
		
		String typename = ref.getTypeName(); 
		
		String entityJSON = InstanceSerialization.toJson(ref, true);
		
		System.out.println("Submitting new entity= " + entityJSON);
        
        JSONObject jsonObject = ac.createEntity(entityJSON);
       
        String guid = jsonObject.getString(AtlasClient.GUID);
        
        System.out.println("created instance for type " + typename + ", guid: " + guid);

        // return the Id for created instance with guid
       
        return new Id(guid, ref.getId().getVersion(), ref.getTypeName());
		
		
	}

}
