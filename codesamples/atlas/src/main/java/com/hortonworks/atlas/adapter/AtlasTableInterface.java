package com.hortonworks.atlas.adapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.apache.atlas.typesystem.persistence.Id;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.atlas.client.AtlasClient;
import com.atlas.client.AtlasServiceException;
import com.google.common.collect.ImmutableList;


/**
 * This is an AtlaTable Interface class.
 * This will load all MySQL Tables into Atlas with Type = "Table"
 * @author sdutta
 *
 */
public class AtlasTableInterface {

	private static final String DATABASE_TYPE = "DB";
	private static final String COLUMN_TYPE = "Column";
	private static final String TABLE_TYPE = "Table";
	private static final String VIEW_TYPE = "View";
	private static final String LOAD_PROCESS_TYPE = "LoadProcess";
	private static final String STORAGE_DESC_TYPE = "StorageDesc";
	
	 public static final String[] TYPES =
         {DATABASE_TYPE, TABLE_TYPE, STORAGE_DESC_TYPE, COLUMN_TYPE, LOAD_PROCESS_TYPE, VIEW_TYPE, "JdbcAccess",
                 "ETL", "Metric", "PII", "Fact", "Dimension"};

	AtlasClient metadataServiceClient = null;
	mysqladapter mysqa =  null;
	
	
	/**
	 * 
	 * @param baseUrl
	 * @param mysqlhost
	 * @param db
	 * @param username
	 * @param password
	 * @throws Exception 
	 */
	public AtlasTableInterface(String baseUrl, String mysqlhost, String db, String username, String password) throws Exception {

		metadataServiceClient = new AtlasClient(baseUrl);
		
		ArrayList<String> types = (ArrayList<String>) metadataServiceClient.listTypes();
		ListIterator<String> typeList = types.listIterator();
		
		for(String type : TYPES){
			if(!types.contains(type)){
				throw new AtlasServiceException("Please execute the default scripts: quick_start.py. Missing Atlas Type: " + types);
			}
		
		}
		
		
		mysqa = new mysqladapter(mysqlhost, db, username, password);
		createMysqlEntities();
		
	}
	
	
	
	/**
	 * @throws Exception 
	 * 
	 */
	
	private void createMysqlEntities() throws Exception{
		
		HashMap<String, Table> hshmap = mysqa.getTableMap();
		
		Iterator<String> itr = hshmap.keySet().iterator();
		
		String table_name = null;
		java.util.ArrayList<Column> clist  = null;
		Iterator<Column> itrc = null;
		Referenceable colref, tabref;
		
		while(itr.hasNext()){
			
			table_name = itr.next();
			Table t = hshmap.get(table_name);
			clist = t.getColumnArrayList();
			itrc = clist.iterator();
			ImmutableList<Referenceable> lst = ImmutableList.of();
			
			while (itrc.hasNext()){
				
				Column c = itrc.next();
			    colref = this.rawColumn(c.getColumn_name(), c.getColumn_type(), c.getColumn_remarks());
			    
			    lst.add(colref);
			    
			    
			}
			
			this.table(t.getTable_name(), t.getRemarks(), database(t.getDb().getName(), "MYSQL DB",  t.getRemarks(), "tbd"), rawStorageDescriptor(t.getDb().getName() + "." + t.getTable_name(),"Table","Binary", true ), t.getDb().getName(), t.getTable_type(),lst, t.getDb().getName());
		
		}
		
		
	}

	
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

	Id database(String name, String description, String owner,
			String locationUri, String... traitNames) throws Exception {
		Referenceable referenceable = new Referenceable(DATABASE_TYPE,
				traitNames);
		referenceable.set("name", name);
		referenceable.set("description", description);
		referenceable.set("owner", owner);
		referenceable.set("locationUri", locationUri);
		referenceable.set("createTime", System.currentTimeMillis());

		return createInstance(referenceable);
	}

	Referenceable rawStorageDescriptor(String location, String inputFormat,
			String outputFormat, boolean compressed) throws Exception {
		Referenceable referenceable = new Referenceable(STORAGE_DESC_TYPE);
		referenceable.set("location", location);
		referenceable.set("inputFormat", inputFormat);
		referenceable.set("outputFormat", outputFormat);
		referenceable.set("compressed", compressed);

		return referenceable;
	}

	Referenceable rawColumn(String name, String dataType, String comment,
			String... traitNames) throws Exception {
		Referenceable referenceable = new Referenceable(COLUMN_TYPE, traitNames);
		referenceable.set("name", name);
		referenceable.set("dataType", dataType);
		referenceable.set("comment", comment);

		return referenceable;
	}

	Id table(String name, String description, Id dbId, Referenceable sd,
			String owner, String tableType, List<Referenceable> columns,
			String... traitNames) throws Exception {
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

	Id loadProcess(String name, String description, String user,
			List<Id> inputTables, List<Id> outputTables, String queryText,
			String queryPlan, String queryId, String queryGraph,
			String... traitNames) throws Exception {
		Referenceable referenceable = new Referenceable(LOAD_PROCESS_TYPE,
				traitNames);
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

	Id view(String name, Id dbId, List<Id> inputTables, String... traitNames)
			throws Exception {
		Referenceable referenceable = new Referenceable(VIEW_TYPE, traitNames);
		referenceable.set("name", name);
		referenceable.set("db", dbId);

		referenceable.set("inputTables", inputTables);

		return createInstance(referenceable);
	}

	private void verifyTypesCreated() throws Exception {
		List<String> types = metadataServiceClient.listTypes();
		for (String type : TYPES) {
			assert types.contains(type);
		}
	}

	private String[] getDSLQueries() {
		return new String[] {
				"from DB",
				"DB",
				"DB where name=\"Reporting\"",
				"DB where DB.name=\"Reporting\"",
				"DB name = \"Reporting\"",
				"DB DB.name = \"Reporting\"",
				"DB where name=\"Reporting\" select name, owner",
				"DB where DB.name=\"Reporting\" select name, owner",
				"DB has name",
				"DB where DB has name",
				"DB, Table",
				"DB is JdbcAccess",
				/*
				 * "DB, hive_process has name",
				 * "DB as db1, Table where db1.name = \"Reporting\"",
				 * "DB where DB.name=\"Reporting\" and DB.createTime < " +
				 * System.currentTimeMillis()},
				 */
				"from Table",
				"Table",
				"Table is Dimension",
				"Column where Column isa PII",
				"View is Dimension",
				/* "Column where Column isa PII select Column.name", */
				"Column select Column.name",
				"Column select name",
				"Column where Column.name=\"customer_id\"",
				"from Table select Table.name",
				"DB where (name = \"Reporting\")",
				"DB where (name = \"Reporting\") select name as _col_0, owner as _col_1",
				"DB where DB is JdbcAccess",
				"DB where DB has name",
				"DB Table",
				"DB where DB has name",
				"DB as db1 Table where (db1.name = \"Reporting\")",
				"DB where (name = \"Reporting\") select name as _col_0, (createTime + 1) as _col_1 ",
				/*
				 * todo: does not work
				 * "DB where (name = \"Reporting\") and ((createTime + 1) > 0)",
				 * "DB as db1 Table as tab where ((db1.createTime + 1) > 0) and
				 * (db1.name = \"Reporting\") select db1.name as dbName,
				 * tab.name as tabName", "DB as db1 Table as tab where
				 * ((db1.createTime + 1) > 0) or (db1.name = \"Reporting\")
				 * select db1.name as dbName, tab.name as tabName", "DB as db1
				 * Table as tab where ((db1.createTime + 1) > 0) and (db1.name =
				 * \"Reporting\") or db1 has owner select db1.name as dbName,
				 * tab.name as tabName", "DB as db1 Table as tab where
				 * ((db1.createTime + 1) > 0) and (db1.name = \"Reporting\") or
				 * db1 has owner select db1.name as dbName, tab.name as
				 * tabName",
				 */
				// trait searches
				"Dimension",
				/* "Fact", - todo: does not work */
				"JdbcAccess",
				"ETL",
				"Metric",
				"PII",
				/*
				 * // Lineage - todo - fix this, its not working
				 * "Table hive_process outputTables",
				 * "Table loop (hive_process outputTables)",
				 * "Table as _loop0 loop (hive_process outputTables) withPath",
				 * "Table as src loop (hive_process outputTables) as dest select
				 * src.name as srcTable, dest.name as destTable withPath",
				 */
				"Table where name=\"sales_fact\", columns",
				"Table where name=\"sales_fact\", columns as column select column.name, column.dataType, column"
						+ ".comment", "from DataSet", "from Process", };
	}

	private void search() throws Exception {
		for (String dslQuery : getDSLQueries()) {
			JSONObject response = metadataServiceClient.searchEntity(dslQuery);
			JSONObject results = response.getJSONObject(AtlasClient.RESULTS);
			if (!results.isNull("rows")) {
				JSONArray rows = results.getJSONArray("rows");
				System.out.println("query [" + dslQuery + "] returned ["
						+ rows.length() + "] rows");
			} else {
				System.out.println("query [" + dslQuery + "] failed, results:"
						+ results.toString());
			}
		}
	}

}
