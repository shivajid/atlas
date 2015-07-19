package com.atlas.client;

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
		
			Referenceable proc = aec.loadProcess("AsteroidConnector", "This  connects 2 Asteroids", ImmutableList.of(inpref.getId()), ImmutableList.of(outref.getId()), "Red");
			
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
	public Referenceable loadProcess(String name, String description, List<Id> inputTables, List<Id> outputTables,
           String... traitNames)
   throws Exception {
       Referenceable referenceable = new Referenceable(AtlasTypeDefCreator.Type_New_Life, traitNames);
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
       
        return 
        		new Id(guid, ref.getId().getVersion(), ref.getTypeName());
		
		
	}
}
