package com.atlas.test;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.atlas.test.AtlasClient;

import org.apache.atlas.hive.model.HiveDataModelGenerator;
import org.apache.atlas.hive.model.HiveDataTypes;
import org.apache.atlas.security.SecureClientUtils;
import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.apache.atlas.typesystem.json.TypesSerialization;
import org.apache.atlas.typesystem.persistence.Id;
import org.apache.atlas.typesystem.types.*;
import org.apache.atlas.typesystem.types.utils.TypesUtil;
import org.apache.atlas.typesystem.TypesDef;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.hive_metastoreConstants;
import org.apache.hadoop.hive.ql.metadata.Table;
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
import java.util.Calendar;
import java.util.List;

/**
 * This is for loading data
 * 
 * @author sdutta
 *
 */
public class MySqlIngester {

	static Logger LOG = LoggerFactory.getLogger(MySqlIngester.class);

	private static final String LOAD_PROCESS_TYPE = "LoadProcess";
	private static final String STORAGE_DESC_TYPE = "StorageDesc";
	private static final String MYSQL_TABLE_TYPE = "demotable_type10";
	private static final String Sqoop_TYPE = "Sqoop_Process_Type2";
	private static final String Falcon_Type = "Falcon_Type";
	private static final String DATABASE_TYPE = "DB";
	private static final String COLUMN_TYPE = "Column";
	private static final String TABLE_TYPE = "Table";
	private static final String VIEW_TYPE = "View";

	private final AtlasClient metadataServiceClient;

	public MySqlIngester(String baseurl) {

		this.metadataServiceClient = new AtlasClient(baseurl);
	}

	private String clustername = null;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		if (args.length < 1) {
			throw new Exception("Please provide the DGI host url");
		}

		System.setProperty("atlas.conf", "/Users/sdutta/Applications/conf");

		String baseUrl = getServerUrl(args);

		MySqlIngester sqlIngester = new MySqlIngester(baseUrl);

		sqlIngester.createTypes();
		System.out.println("Creating Entitites");
		sqlIngester.createEntities("testers", "this is data being laoded",
				"TestDB");

	}

	/*
	 * This method creates a Type
	 */

	void createTypes() throws Exception {

		TypesDef typesDef = this.createMysqlTypes();

		String typesAsJSON = TypesSerialization.toJson(typesDef);

		System.out.println("typesAsJSON = " + typesAsJSON);

		
	  this.metadataServiceClient.createType(typesAsJSON);

		System.out.println("MySQL Type System Created");

	}

	/**
	 * This creates of MysqlType
	 * 
	 * @return
	 * @throws Exception
	 */
	public TypesDef createMysqlTypes() throws Exception {

		HierarchicalTypeDefinition<ClassType> mysqlTable = TypesUtil
				.createClassTypeDef(this.MYSQL_TABLE_TYPE, null, this.attrDef("name",
						DataTypes.STRING_TYPE), this.attrDef("description",
						DataTypes.STRING_TYPE), this.attrDef("sourceDB",
						DataTypes.STRING_TYPE),  this.attrDef("destinationDB",
								DataTypes.STRING_TYPE));

		HierarchicalTypeDefinition<ClassType> sqoopProcess = TypesUtil
				.createClassTypeDef(
						this.Sqoop_TYPE,
						ImmutableList.of("Process"),
						attrDef("command", DataTypes.STRING_TYPE));
/*
		HierarchicalTypeDefinition<ClassType> falconProcess = TypesUtil
				.createClassTypeDef(
						this.Falcon_Type,
						ImmutableList.of("Process"),
						attrDef("entityName", DataTypes.STRING_TYPE,
								Multiplicity.REQUIRED));

		HierarchicalTypeDefinition<TraitType> dimTraitDef = TypesUtil
				.createTraitTypeDef("Dimension", null);

		HierarchicalTypeDefinition<TraitType> factTraitDef = TypesUtil
				.createTraitTypeDef("Fact", null);

		HierarchicalTypeDefinition<TraitType> piiTraitDef = TypesUtil
				.createTraitTypeDef("PII", null);

		HierarchicalTypeDefinition<TraitType> metricTraitDef = TypesUtil
				.createTraitTypeDef("Metric", null);

		HierarchicalTypeDefinition<TraitType> etlTraitDef = TypesUtil
				.createTraitTypeDef("ETL", null);

		HierarchicalTypeDefinition<TraitType> valueTraitDef = TypesUtil
				.createTraitTypeDef("Value", null);
*/
		return TypeUtils.getTypesDef(ImmutableList.<EnumTypeDefinition> of(),
				ImmutableList.<StructTypeDefinition> of(),
				ImmutableList.<HierarchicalTypeDefinition<TraitType>>of(),
				//ImmutableList.of(dimTraitDef, factTraitDef, piiTraitDef,
					//	metricTraitDef, etlTraitDef, valueTraitDef),

				ImmutableList.of(mysqlTable,sqoopProcess));

	}

	/**
	 * 
	 * @param name
	 * @param dataType
	 * @param comment
	 * @param traitNames
	 * @return
	 * @throws Exception
	 */
	Referenceable rawColumn(String name, String dataType, String comment,
			String... traitNames) throws Exception {
		Referenceable referenceable = new Referenceable(COLUMN_TYPE, traitNames);
		referenceable.set("name", name);
		referenceable.set("dataType", dataType);
		referenceable.set("comment", comment);

		return referenceable;
	}

	/**
	 * 
	 * @param tablename
	 * @param tabledescription
	 * @param sourceDB
	 * @throws Exception
	 */
	public void createEntities(String tablename, String tabledescription,
			String sourceDB) throws Exception {

		// Id salesDB = database("Sales", "Sales Database", "John ETL",
		// "hdfs://host:8000/apps/warehouse/sales");

		// Referenceable sd =
		// rawStorageDescriptor("hdfs://host:8000/apps/warehouse/sales",
		// "TextInputFormat", "TextOutputFormat",
		// true);

		List<Referenceable> salesFactColumns = ImmutableList.of(
				rawColumn("time_id", "int", "time id"),
				rawColumn("product_id", "int", "product id"),
				rawColumn("customer_id", "int", "customer id", "PII"),
				rawColumn("sales", "double", "product id", "Metric"));

		Id mysqlFact = mysqltable(tablename + "source", sourceDB, tabledescription, "Hive");

		//Id hivetable = registerTable("default", tablename + "_hive");
		
		Id hivetable = mysqltable(tablename + "destination", sourceDB, tabledescription, "Hive");

		loadProcess("sqlingestion", "mysql ingestion of data - Sqoop Process",
				ImmutableList.of(mysqlFact), ImmutableList.of(hivetable),
				"PII", "ETL");

	}

	/**
	 *
	 */
	Id mysqltable(String name, String sourcedb, String description,
			String destdb, String... traitNames) throws Exception {
		Referenceable referenceable = new Referenceable(this.MYSQL_TABLE_TYPE,
				traitNames);
		referenceable.set("name", name);
		referenceable.set("description", description);
		referenceable.set("sourcedb", sourcedb);
		referenceable.set("destinationdb", destdb);

		return createInstance(referenceable);
	}

	/*
	 * Id table(String name, String description, Id dbId, Referenceable sd,
	 * String owner, String tableType, List<Referenceable> columns, String...
	 * traitNames) throws Exception {
	 * 
	 * Referenceable referenceable = new Referenceable(TABLE_TYPE, traitNames);
	 * referenceable.set("name", name); referenceable.set("description",
	 * description); referenceable.set("owner", owner);
	 * referenceable.set("tableType", tableType);
	 * referenceable.set("createTime", System.currentTimeMillis());
	 * referenceable.set("lastAccessTime", System.currentTimeMillis());
	 * referenceable.set("retention", System.currentTimeMillis());
	 * referenceable.set("db", dbId); referenceable.set("sd", sd);
	 * referenceable.set("columns", columns);
	 * 
	 * return createInstance(referenceable); }
	 */

	Id loadProcess(String name, String description, List<Id> inputTables,
			List<Id> outputTables, String... traitNames) throws Exception {
		Referenceable referenceable = new Referenceable(this.Sqoop_TYPE);
		// super type attributes
	//	referenceable.set("entityName", name);
		//referenceable.set("entityDescription", description);
		referenceable.set("inputs", inputTables);
		referenceable.set("outputs", outputTables);
		referenceable.set("command", "sqoop -import ....");

		return createInstance(referenceable);
	}

	/**
	 * This will register the DB
	 * 
	 */
	public Referenceable registerDatabase(String databaseName,
			String clusterName, String hiveDBName, String HiveDBDescription,
			String location, String parameter, String owner) throws Exception {

		Referenceable dbRef = new Referenceable(HiveDataTypes.HIVE_DB.getName());
		
		dbRef.set(HiveDataModelGenerator.NAME, hiveDBName.toLowerCase());
		dbRef.set(HiveDataModelGenerator.CLUSTER_NAME, clusterName);
		dbRef.set("description", HiveDBDescription);
		dbRef.set("locationUri", location);
		dbRef.set("parameters", parameter);
		dbRef.set("ownerName", owner);

		dbRef = createInstance2(dbRef);

		return dbRef;
	}

	
	
	/**
	 * 
	 * @param dbRef
	 * @param dbName
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public Referenceable registerTable(String dbRef, String dbName,
			String tableName) throws Exception {

		return registerTable(dbRef, dbName, tableName);
	}

	
	
	/*
	 * private Referenceable getTableReference(String dbName, String tableName)
	 * throws Exception { LOG.debug("Getting reference for table {}.{}", dbName,
	 * tableName);
	 * 
	 * String typeName = HiveDataTypes.HIVE_TABLE.getName(); String entityName =
	 * getTableName(clusterName, dbName, tableName); String dslQuery =
	 * String.format("%s as t where name = '%s'", typeName, entityName); return
	 * getEntityReferenceFromDSL(typeName, dslQuery); }
	 */

	public Id registerTable(String dbName, String tableName) throws Exception {
		LOG.info("Attempting to register table [" + tableName + "]");

		Referenceable tableRef = null;

		LOG.info("Importing objects from " + dbName + "." + tableName);

		// Table hiveTable = tableName;

		tableRef = new Referenceable(HiveDataTypes.HIVE_TABLE.getName());
		tableRef.set(HiveDataModelGenerator.NAME, tableName);
		tableRef.set(HiveDataModelGenerator.TABLE_NAME, tableName.toLowerCase());
		tableRef.set("owner", "HWX");

		tableRef.set("createTime", Calendar.getInstance().getTime()
				.toLocaleString());
		tableRef.set("lastAccessTime", "10");
		tableRef.set("retention", "10");

		tableRef.set(HiveDataModelGenerator.COMMENT,
				"This Table is generated by SQL Ingenstion");

		
		 // add reference to the database
		 tableRef.set(HiveDataModelGenerator.DB, "Default");
		 
		 //List<Referenceable> colList = getColumns(hiveTable.getCols());
		  tableRef.set("columns", ImmutableList.of("driver_id", "driver_name"));
		  
		  // add reference to the StorageDescriptor StorageDescriptor
		  //storageDesc = hiveTable.getSd(); 
		  
		  //Referenceable sdReferenceable =
		  //fillStorageDescStruct(storageDesc, colList); 
		  
		  tableRef.set("sd",
		  "table");
		 
		  // add reference to the Partition Keys List<Referenceable> partKeys =
		 // getColumns(hiveTable.getPartitionKeys());
		  tableRef.set("partitionKeys", "driverid");
		 
		  tableRef.set("parameters", "noparam");
		  
		 // if (hiveTable.getViewOriginalText() != null) {
			  tableRef.set("viewOriginalText", "original text"); 
		  
		  //if (hiveTable.getViewExpandedText() != null) {
			  tableRef.set("viewExpandedText", "expanded view"); 
		 
		tableRef.set("tableType", HiveDataTypes.HIVE_TABLE.getName());
		tableRef.set("temporary", false);

		Id id = createInstance(tableRef);

		return id;
	}

	/*
	 * Creates a Reference and returns an Instance
	 */
	public Referenceable createInstance2(Referenceable referenceable)
			throws Exception {
		String typeName = referenceable.getTypeName();
		LOG.debug("creating instance of type " + typeName);

		String entityJSON = InstanceSerialization.toJson(referenceable, true);
		LOG.debug("Submitting new entity {} = {}", referenceable.getTypeName(),
				entityJSON);
		JSONObject jsonObject = this.metadataServiceClient
				.createEntity(entityJSON);
		String guid = jsonObject.getString(AtlasClient.GUID);
		LOG.debug("created instance for type " + typeName + ", guid: " + guid);

		return new Referenceable(guid, referenceable.getTypeName(), null);
	}

	/*
	 * TypesDef createTypeDefinitions() throws Exception {
	 * 
	 * 
	 * HierarchicalTypeDefinition<ClassType> transportClsDef = TypesUtil
	 * .createClassTypeDef(this.TRANSPORT_TYPE, null, attrDef("name",
	 * DataTypes.STRING_TYPE), attrDef("description", DataTypes.STRING_TYPE),
	 * attrDef("locationUri", DataTypes.STRING_TYPE), attrDef("owner",
	 * DataTypes.STRING_TYPE), attrDef("createTime", DataTypes.INT_TYPE));
	 * 
	 * HierarchicalTypeDefinition<ClassType> carrierClsDef = TypesUtil
	 * .createClassTypeDef(this.CARRIER_TYPE, null, attrDef("name",
	 * DataTypes.STRING_TYPE), attrDef("location", DataTypes.STRING_TYPE),
	 * attrDef("country", DataTypes.STRING_TYPE), attrDef("CEO",
	 * DataTypes.STRING_TYPE) );
	 * 
	 * HierarchicalTypeDefinition<ClassType> routeClsDef = TypesUtil
	 * .createClassTypeDef(this.ROUTE_TYPE, null, attrDef("name",
	 * DataTypes.STRING_TYPE), attrDef("route_id", DataTypes.STRING_TYPE),
	 * attrDef("comment", DataTypes.STRING_TYPE));
	 * 
	 * HierarchicalTypeDefinition<ClassType> motionClsDef = TypesUtil
	 * .createClassTypeDef(this.MOTION_TYPE, null, attrDef("rating",
	 * DataTypes.STRING_TYPE), attrDef("metrics", DataTypes.STRING_TYPE),
	 * attrDef("comment", DataTypes.STRING_TYPE));
	 * 
	 * 
	 * HierarchicalTypeDefinition<TraitType> dimTraitDef =
	 * TypesUtil.createTraitTypeDef("Dimension", null);
	 * 
	 * HierarchicalTypeDefinition<TraitType> factTraitDef =
	 * TypesUtil.createTraitTypeDef("Fact", null);
	 * 
	 * HierarchicalTypeDefinition<TraitType> piiTraitDef =
	 * TypesUtil.createTraitTypeDef("PII", null);
	 * 
	 * HierarchicalTypeDefinition<TraitType> metricTraitDef =
	 * TypesUtil.createTraitTypeDef("Metric", null);
	 * 
	 * HierarchicalTypeDefinition<TraitType> etlTraitDef =
	 * TypesUtil.createTraitTypeDef("ETL", null);
	 * 
	 * return TypeUtils.getTypesDef(ImmutableList.<EnumTypeDefinition>of(),
	 * ImmutableList.<StructTypeDefinition>of(),
	 * 
	 * ImmutableList.<HierarchicalTypeDefinition<TraitType>>of(),
	 * //ImmutableList.of(dimTraitDef, factTraitDef, piiTraitDef,
	 * metricTraitDef, etlTraitDef),
	 * 
	 * ImmutableList.of(transportClsDef, carrierClsDef, routeClsDef,
	 * motionClsDef)); }
	 */

	AttributeDefinition attrDef(String name, IDataType dT) {
		return attrDef(name, dT, Multiplicity.OPTIONAL, false, null);
	}

	AttributeDefinition attrDef(String name, IDataType dT, Multiplicity m) {
		return attrDef(name, dT, m, false, null);
	}

	AttributeDefinition attrDef(String name, IDataType dT, Multiplicity m,
			boolean isComposite, String reverseAttributeName) {
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(dT);
		return new AttributeDefinition(name, dT.getName(), m, isComposite,
				reverseAttributeName);
	}

	/**
	 * This creates a new Client
	 * 
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

		System.out.println("created instance for type " + typeName + ", guid: "
				+ guid);

		// return the Id for created instance with guid
		return new Id(guid, referenceable.getId().getVersion(),
				referenceable.getTypeName());
	}

	/**
	 * Create Entities for the type definitions. Types can be class, struct or a
	 * Java cas
	 * 
	 * @throws Exception
	 */
	/*
	 * void createEntities() throws Exception {
	 * 
	 * Id boeingDB = transport("Boeing 747", "Best Plane in the United States",
	 * "James McNeary", "http://wwww.boeing.com");
	 * 
	 * Referenceable carrier = carrier("United Airlines", "San Francisco",
	 * "USA", "James McNeary");
	 * 
	 * Id carrierId = this.createInstance(carrier); }
	 */
	/*
	 * Id transport(String name, String description, String owner, String
	 * locationUri, String... traitNames) throws Exception { Referenceable
	 * referenceable = new Referenceable(this.TRANSPORT_TYPE, traitNames);
	 * referenceable.set("name", name); referenceable.set("description",
	 * description); referenceable.set("owner", owner);
	 * referenceable.set("locationUri", locationUri);
	 * referenceable.set("createTime", System.currentTimeMillis());
	 * 
	 * return createInstance(referenceable); }
	 * 
	 * 
	 * 
	 * 
	 * Referenceable carrier(String name, String location, String country,
	 * String CEO) throws Exception { Referenceable referenceable = new
	 * Referenceable(this.CARRIER_TYPE); referenceable.set("name", name);
	 * referenceable.set("location", location); referenceable.set("country",
	 * country); referenceable.set("CEO", CEO);
	 * 
	 * return referenceable; }
	 * 
	 * Referenceable route(String name, String route_id, String comment ) throws
	 * Exception { Referenceable referenceable = new
	 * Referenceable(this.ROUTE_TYPE); referenceable.set("name", name);
	 * referenceable.set("dataType", route_id); referenceable.set("comment",
	 * comment);
	 * 
	 * return referenceable; }
	 * 
	 * 
	 * Referenceable motion(String rating, String metrics, String comment )
	 * throws Exception { Referenceable referenceable = new
	 * Referenceable(this.MOTION_TYPE); referenceable.set("rating", rating);
	 * referenceable.set("metrics", metrics); referenceable.set("comment",
	 * comment);
	 * 
	 * return referenceable; }
	 */

	/*
	 * Id table(String name, String description, Id dbId, Referenceable sd,
	 * String owner, String tableType, List<Referenceable> columns, String...
	 * traitNames) throws Exception {
	 * 
	 * Referenceable referenceable = new Referenceable(TABLE_TYPE, traitNames);
	 * referenceable.set("name", name); referenceable.set("description",
	 * description); referenceable.set("owner", owner);
	 * referenceable.set("tableType", tableType);
	 * referenceable.set("createTime", System.currentTimeMillis());
	 * referenceable.set("lastAccessTime", System.currentTimeMillis());
	 * referenceable.set("retention", System.currentTimeMillis());
	 * referenceable.set("db", dbId); referenceable.set("sd", sd);
	 * referenceable.set("columns", columns);
	 * 
	 * return createInstance(referenceable); }
	 * 
	 * Id loadProcess(String name, String description, String user, List<Id>
	 * inputTables, List<Id> outputTables, String queryText, String queryPlan,
	 * String queryId, String queryGraph, String... traitNames) throws Exception
	 * { Referenceable referenceable = new Referenceable(LOAD_PROCESS_TYPE,
	 * traitNames); // super type attributes referenceable.set("name", name);
	 * referenceable.set("description", description);
	 * referenceable.set("inputs", inputTables); referenceable.set("outputs",
	 * outputTables);
	 * 
	 * referenceable.set("user", user); referenceable.set("startTime",
	 * System.currentTimeMillis()); referenceable.set("endTime",
	 * System.currentTimeMillis() + 10000);
	 * 
	 * referenceable.set("queryText", queryText); referenceable.set("queryPlan",
	 * queryPlan); referenceable.set("queryId", queryId);
	 * referenceable.set("queryGraph", queryGraph);
	 * 
	 * return createInstance(referenceable); }
	 */

	/*
	 * This function gets the data
	 */
	static String getServerUrl(String[] args) {
		String baseUrl = "http://atlasdemo.cloud.hortonworks.com:21000";
		if (args.length > 0) {
			baseUrl = args[0];
		}

		System.out.println(baseUrl);
		return baseUrl;
	}

}
