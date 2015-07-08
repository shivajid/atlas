package com.atlas.client;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.atlas.client.AtlasClient;

import org.apache.atlas.security.SecureClientUtils;
import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.apache.atlas.typesystem.json.TypesSerialization;
import org.apache.atlas.typesystem.persistence.Id;
import org.apache.atlas.typesystem.types.*;
import org.apache.atlas.typesystem.types.utils.TypesUtil;
import org.apache.atlas.typesystem.TypesDef;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.util.ArrayList;
import java.util.List;



/**
 * This is for loading data
 * @author sdutta
 *
 */
public class DemoClass {

	static Logger logger = LoggerFactory.getLogger(DemoClass.class);
	
	 	private static final String TRANSPORT_TYPE = "AeroPlane";
	    private static final String CARRIER_TYPE = "Manufacturing";
	    private static final String ROUTE_TYPE = "Air";
	    private static final String MOTION_TYPE = "Speed";
	    
	//    private static final String LOAD_PROCESS_TYPE = "LoadProcess";
	//    private static final String STORAGE_DESC_TYPE = "StorageDesc";
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		if(args.length < 1)
		{	
			throw new Exception("Please provide the DGI host url");
		}
		
		System.setProperty("atlas.conf", "/Users/sdutta/Applications/conf");
		
		String baseUrl = getServerUrl(args);
		
		DemoClass dc = new DemoClass(baseUrl);
		 dc.createTypes();
		 
		 // Shows how to create types in Atlas for your meta model
	        dc.createTypes();

	        // Shows how to create entities (instances) for the added types in Atlas
	        dc.createEntities();

	        // Shows some search queries using DSL based on types
	        //dc.search();
		
	}
	
	private static final String[] TYPES =
        {TRANSPORT_TYPE, CARRIER_TYPE, ROUTE_TYPE, MOTION_TYPE,};
	
	private final AtlasClient metadataServiceClient;
	
	public DemoClass(String baseurl){
		
		this.metadataServiceClient = new AtlasClient(baseurl);
	}
	

    void createTypes() throws Exception {
        TypesDef typesDef = createTypeDefinitions();

        String typesAsJSON = TypesSerialization.toJson(typesDef);
        
        System.out.println("typesAsJSON = " + typesAsJSON);
        
       metadataServiceClient.createType(typesAsJSON);

        // verify types created
        verifyTypesCreated();
    }
    
    
    /*
     * This API will list the types on the system
     */
    private void verifyTypesCreated() throws Exception {
        List<String> types = metadataServiceClient.listTypes();
        for (String type : TYPES) {
        	System.out.println(type);
            assert types.contains(type);
        }
    }
    
    
    
    TypesDef createTypeDefinitions() throws Exception {
    	
    	
        HierarchicalTypeDefinition<ClassType> transportClsDef = TypesUtil
                .createClassTypeDef(this.TRANSPORT_TYPE, null, attrDef("name", DataTypes.STRING_TYPE),
                        attrDef("description", DataTypes.STRING_TYPE), attrDef("locationUri", DataTypes.STRING_TYPE),
                        attrDef("owner", DataTypes.STRING_TYPE), attrDef("createTime", DataTypes.INT_TYPE));

        HierarchicalTypeDefinition<ClassType> carrierClsDef = TypesUtil
                .createClassTypeDef(this.CARRIER_TYPE, null, 
                		attrDef("name", DataTypes.STRING_TYPE),
                        attrDef("location", DataTypes.STRING_TYPE), 
                        attrDef("country", DataTypes.STRING_TYPE),
                        attrDef("CEO", DataTypes.STRING_TYPE)
                       );

        HierarchicalTypeDefinition<ClassType> routeClsDef = TypesUtil
                .createClassTypeDef(this.ROUTE_TYPE, null, 
                		attrDef("name", DataTypes.STRING_TYPE),
                        attrDef("route_id", DataTypes.STRING_TYPE), 
                        attrDef("comment", DataTypes.STRING_TYPE));

        HierarchicalTypeDefinition<ClassType> motionClsDef = TypesUtil
                .createClassTypeDef(this.MOTION_TYPE, null, 
                		attrDef("rating", DataTypes.STRING_TYPE),
                        attrDef("metrics", DataTypes.STRING_TYPE), 
                        attrDef("comment", DataTypes.STRING_TYPE));

       
        HierarchicalTypeDefinition<TraitType> dimTraitDef = TypesUtil.createTraitTypeDef("Dimension", null);

        HierarchicalTypeDefinition<TraitType> factTraitDef = TypesUtil.createTraitTypeDef("Fact", null);

        HierarchicalTypeDefinition<TraitType> piiTraitDef = TypesUtil.createTraitTypeDef("PII", null);

        HierarchicalTypeDefinition<TraitType> metricTraitDef = TypesUtil.createTraitTypeDef("Metric", null);

        HierarchicalTypeDefinition<TraitType> etlTraitDef = TypesUtil.createTraitTypeDef("ETL", null);

        /**
         * List of 
         */
        return TypeUtils.getTypesDef(ImmutableList.<EnumTypeDefinition>of(), 
        		ImmutableList.<StructTypeDefinition>of(),
        
        		ImmutableList.<HierarchicalTypeDefinition<TraitType>>of(),
        		//ImmutableList.of(dimTraitDef, factTraitDef, piiTraitDef, metricTraitDef, etlTraitDef),
                
                ImmutableList.of(transportClsDef, carrierClsDef, routeClsDef, motionClsDef));
    }

    AttributeDefinition attrDef(String name, IDataType dT) {
        return attrDef(name, dT, Multiplicity.OPTIONAL, false, null);
    }
    
    AttributeDefinition attrDef(String name, IDataType dT, Multiplicity m) {
        return attrDef(name, dT, m, false, null);
    }

    AttributeDefinition attrDef(String name, IDataType dT, Multiplicity m, boolean isComposite,
            String reverseAttributeName) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(dT);
        return new AttributeDefinition(name, dT.getName(), m, isComposite, reverseAttributeName);
    }
    
    
    /**
     * This creates a new Client
     * @param referenceable
     * @return
     * @throws Exception
     */
    private Id createInstance(Referenceable referenceable) throws Exception {
     
    	
    	String typeName = referenceable.getTypeName();

        String entityJSON = InstanceSerialization.toJson(referenceable, true);
        
        System.out.println("Submitting new entity= " + entityJSON);
        
        JSONObject jsonObject = metadataServiceClient.createEntity(entityJSON);
        String guid = jsonObject.getString(AtlasClient.GUID);
        
        System.out.println("created instance for type " + typeName + ", guid: " + guid);

        // return the Id for created instance with guid
        return new Id(guid, referenceable.getId().getVersion(), referenceable.getTypeName());
    }
    
    
    /**
     * Create Entities for the type definitions.
     * Types can be class, struct or a Java cas
     * @throws Exception
     */
    
    void createEntities() throws Exception {

    	Id boeingDB = transport("Boeing 747", "Best Plane in the United States", "James McNeary", "http://wwww.boeing.com");
    	 
        Referenceable carrier =
                carrier("United Airlines", "San Francisco", "USA",
                        "James McNeary");
       

       Id carrierId = this.createInstance(carrier);
    }
    
    Id transport(String name, String description, String owner, String locationUri, String... traitNames)
    	    throws Exception {
    	        Referenceable referenceable = new Referenceable(this.TRANSPORT_TYPE, traitNames);
    	        referenceable.set("name", name);
    	        referenceable.set("description", description);
    	        referenceable.set("owner", owner);
    	        referenceable.set("locationUri", locationUri);
    	        referenceable.set("createTime", System.currentTimeMillis());

    	        return createInstance(referenceable);
    	    }
    
    
  

    	    Referenceable carrier(String name, String location, String country, String CEO)
    	    throws Exception {
    	        Referenceable referenceable = new Referenceable(this.CARRIER_TYPE);
    	        referenceable.set("name", name);
    	        referenceable.set("location", location);
    	        referenceable.set("country", country);
    	        referenceable.set("CEO", CEO);

    	        return referenceable;
    	    }

    	    Referenceable route(String name, String route_id, String comment ) throws Exception {
    	        Referenceable referenceable = new Referenceable(this.ROUTE_TYPE);
    	        referenceable.set("name", name);
    	        referenceable.set("dataType", route_id);
    	        referenceable.set("comment", comment);

    	        return referenceable;
    	    }
    	    
    	    
    	    Referenceable motion(String rating, String metrics, String comment ) throws Exception {
    	        Referenceable referenceable = new Referenceable(this.MOTION_TYPE);
    	        referenceable.set("rating", rating);
    	        referenceable.set("metrics", metrics);
    	        referenceable.set("comment", comment);

    	        return referenceable;
    	    }
    	    
    	    

    	  /*  Id table(String name, String description, Id dbId, Referenceable sd, String owner, String tableType,
    	            List<Referenceable> columns, String... traitNames) throws Exception {
    	    	
    	        Referenceable referenceable = new Referenceable(TABLE_TYPE, traitNames);
    	        referenceable.set("name", name);
    	        referenceable.set("description", description);
    	        referenceable.set("owner", owner);
    	        referenceable.set("tableType", tableType);
    	        referenceable.set("createTime", System.currentTimeMillis());
    	        referenceable.set("lastAccessTime", System.currentTimeMillis());
    	        referenceable.set("retention", System.currentTimeMillis());
    	        referenceable.set("db", dbId);
    	        referenceable.set("sd", sd);
    	        referenceable.set("columns", columns);

    	        return createInstance(referenceable);
    	    }

    	    Id loadProcess(String name, String description, String user, List<Id> inputTables, List<Id> outputTables,
    	            String queryText, String queryPlan, String queryId, String queryGraph, String... traitNames)
    	    throws Exception {
    	        Referenceable referenceable = new Referenceable(LOAD_PROCESS_TYPE, traitNames);
    	        // super type attributes
    	        referenceable.set("name", name);
    	        referenceable.set("description", description);
    	        referenceable.set("inputs", inputTables);
    	        referenceable.set("outputs", outputTables);

    	        referenceable.set("user", user);
    	        referenceable.set("startTime", System.currentTimeMillis());
    	        referenceable.set("endTime", System.currentTimeMillis() + 10000);

    	        referenceable.set("queryText", queryText);
    	        referenceable.set("queryPlan", queryPlan);
    	        referenceable.set("queryId", queryId);
    	        referenceable.set("queryGraph", queryGraph);

    	        return createInstance(referenceable);
    	    }
    */
    
	
	/*
	 * This function gets the data
	 */
	 static String getServerUrl(String[] args) {
	        String baseUrl = "http://http://atlas-partner-demo01.cloud.hortonworks.com:21000";
	        if (args.length > 0) {
	            baseUrl = args[0];
	        }

	        System.out.println(baseUrl);
	        return baseUrl;
	    }
	 
	 
	 
	 

}
