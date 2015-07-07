package com.atlas.test;

import java.util.List;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.apache.atlas.typesystem.persistence.Id;
import org.apache.atlas.typesystem.types.AttributeDefinition;
import org.apache.atlas.typesystem.types.ClassType;
import org.apache.atlas.typesystem.types.DataTypes;
import org.apache.atlas.typesystem.types.HierarchicalTypeDefinition;
import org.apache.atlas.typesystem.types.IDataType;
import org.apache.atlas.typesystem.types.Multiplicity;
import org.apache.atlas.typesystem.types.utils.TypesUtil;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;


/**
 * 
 * 
 * @author sdutta
 *
 */
public class AtlasEntityCreator {

	{
		System.setProperty("atlas.conf", "/Users/sdutta/Applications/conf");
	}
	
	private  AtlasClient ac = null;
	
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		if(args.length < 0)
			throw new Exception("Please pass the atlas base url");
		
		String baseurl = args[0];
		
		System.out.println(" Baseurl" + baseurl);
		AtlasEntityCreator aEC = new AtlasEntityCreator(baseurl);
		//aEC.defineSimpleEntities();
		
		aEC.defineDataSetEntities();
		
		aEC.destroy();
		
	}
	
	
	
	/**
	 * We created 3 types in the previous example
	 * @throws Exception 
	 * 
	 * 
	 */
	private void defineSimpleEntities() throws Exception {
		
		Referenceable createuniveralEntity = createRefObject(AtlasTypeDefCreator.Type_GOD, "Venkatesh", "He is the GOD of DGI");
		
		Referenceable creategeneralEntity = createRefObject(AtlasTypeDefCreator.Type_Planets, "Planet_Earth", "I am part of Solar System");
         
		Id universalObjId = this.createEntity(createuniveralEntity);
		
		Id generalObjId = this.createEntity(creategeneralEntity);
		
		
	}
	
	
	
	/**
	 * 
	 * @throws Exception
	 */
	private void defineDataSetEntities() throws Exception {

		List<Referenceable> timeDimColumns = ImmutableList
                .of(rawColumn("time_id", "int", "time id"), rawColumn("dayOfYear", "int", "day Of Year"),
                        rawColumn("weekDay", "int", "week Day"));
		
		  Id Aster1 =
	                table("Aster1", "customer dimension table", timeDimColumns, 1000, "1 lightyear",  
	                        "White");
		
		  List<Referenceable> spaceDimColumns = ImmutableList
	                .of(rawColumn("space_id", "int", "space id"), rawColumn("timeOfYear", "int", "time Of Year"),
	                        rawColumn("weekofDay", "int", "week Day"));
			
			  Id Aster2 =
		                table("Aster2", "customer dimension table", timeDimColumns, 1000, "1 lightyear",  
		                        "White");
			
			 
		
		
	}
	 
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @param columns
	 * @param traitNames
	 * @return
	 * @throws Exception
	 */
	 
	Id table(String name, String description, List<Referenceable> columns, int speed, String dist, String... traitNames) throws Exception {
	        Referenceable referenceable = new Referenceable(AtlasTypeDefCreator.Type_Asteroids, traitNames);
	        referenceable.set("name", name);
	        referenceable.set("description", description);
	        referenceable.set("createTime", System.currentTimeMillis());
	        referenceable.set("lastAccessTime", System.currentTimeMillis());
	        referenceable.set("speed", speed);
	        referenceable.set("distance_frm_Earth", dist);
	
	        referenceable.set("columns", columns);

	        return createEntity(referenceable);
	    }
	 
	 
	
	/**
	 * 
	 * @param name
	 * @param comment
	 * @return
	 * @throws Exception
	 */
	  Referenceable createRefObject(String type, String name, String description)
	    	    throws Exception {
	    	        Referenceable referenceable = new Referenceable(type);
	    	        referenceable.set("name", name);
	    	        referenceable.set("description", description);
	    	        	    	    
	    	        return referenceable;
	    	    }
	
	  
	/**
	 * 
	 * @param baseurl
	 */
	public  AtlasEntityCreator(String baseurl){
		
		System.out.println("Creating Client Connection" + baseurl);
		ac = new AtlasClient(baseurl);
		System.out.println("Client Object returned");
	
		
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
	
	
	Referenceable rawColumn(String name, String dataType, String comment, String... traitNames) throws Exception {
	        Referenceable referenceable = new Referenceable(AtlasTypeDefCreator.COLUMN_TYPE, traitNames);
	        referenceable.set("name", name);
	        referenceable.set("dataType", dataType);
	        referenceable.set("comment", comment);

	        return referenceable;
	    }
	  
	
	/**
	 * 
	 * 
	 */
	public void destroy() {
		
		ac = null;
	}

	
}
