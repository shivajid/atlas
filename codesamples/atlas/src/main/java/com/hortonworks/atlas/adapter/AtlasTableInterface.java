package com.hortonworks.atlas.adapter;

import java.io.Console;
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
import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.hortonworks.atlas.cli.AtlasCLIOptions;
import com.hortonworks.atlas.client.AtlasEntityConnector;
import com.hortonworks.atlas.client.HiveMetaDataGenerator;

/**
 * This is an AtlaTable Interface class. This will load all MySQL Tables into
 * Atlas with Type = "Table"
 * 
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
	private String clustername = null;
	private boolean hiveExecflag = false;
	private boolean genlineage = false;
	private boolean suppressprompt = false;

	public static final String[] TYPES = { DATABASE_TYPE, TABLE_TYPE,
			STORAGE_DESC_TYPE, COLUMN_TYPE, LOAD_PROCESS_TYPE, VIEW_TYPE,
			"JdbcAccess", "ETL", "Metric", "PII", "Fact", "Dimension" };

	AtlasClient metadataServiceClient = null;
	MySqlAdapter mysqa = null;

	/**
	 * 
	 * @param baseUrl
	 * @param mysqlhost
	 * @param db
	 * @param username
	 * @param password
	 * @throws Exception
	 */
	public AtlasTableInterface(String baseUrl, String mysqlhost, String db,
			String username, String password, boolean hiveflag,
			String ambariclustername, boolean generateLineage, boolean suppress)
			throws Exception {

		metadataServiceClient = new AtlasClient(baseUrl);

		ArrayList<String> types = (ArrayList<String>) metadataServiceClient
				.listTypes();
		ListIterator<String> typeList = types.listIterator();
		this.genlineage = generateLineage;

		for (String type : TYPES) {
			if (!types.contains(type)) {
				throw new Exception(
						"Please execute the default scripts: quick_start.py. Missing Atlas Type: "
								+ types);
			}

		}
		this.clustername = ambariclustername;
		this.hiveExecflag = hiveflag;
		this.genlineage = generateLineage;
		this.suppressprompt =  suppress;
		
		mysqa = new MySqlAdapter(mysqlhost, db, username, password);
		createMysqlEntities();

	}

	/**
	 * 
	 * @throws Exception
	 */

	private void createMysqlEntities() throws Exception {

		HashMap<String, Table> hshmap = mysqa.getTableMap();

		Iterator<String> itr = hshmap.keySet().iterator();

		String table_name = null;
		java.util.ArrayList<Column> clist = null;
		Iterator<Column> itrc = null;
		Referenceable colref, hivetabref = null;
		AtlasEntityConnector aec = new AtlasEntityConnector();

		while (itr.hasNext()) {

			table_name = itr.next();

			if (!this.suppressprompt) {
			
				Console console = System.console();
				String input = console.readLine("Do you want to import table "
						+ table_name + " (y/n):");

				if ("n".equalsIgnoreCase(input)) {
					System.out.println("Table " + table_name
							+ " will not be imported");
					continue;
				}
			}
			
			Table t = hshmap.get(table_name);

			System.out.println(t.getTable_name());

			System.out.println(t.getDb().getName());

			clist = t.getColumnArrayList();
			itrc = clist.iterator();
			ImmutableList<Referenceable> lst = ImmutableList.of();

			Builder bld = ImmutableList.<Referenceable> builder();

			while (itrc.hasNext()) {
				Column c = itrc.next();
				colref = this.rawColumn(c.getColumn_name(), c.getColumn_type(),
						c.getColumn_remarks());
				bld.add(colref);

			}

			lst = bld.build();

			Id tabid = this.table(
					t.getTable_name(),
					t.getRemarks(),
					database(t.getDb().getName(), "MYSQL DB", t.getRemarks(),
							"tbd"),
					rawStorageDescriptor(
							t.getDb().getName() + "." + t.getTable_name(),
							"Table", "Binary", true), t.getDb().getName(), t
							.getTable_type(), lst);

			if (this.hiveExecflag) {
				System.out.println("Create hive tables ..");
				HiveMetaDataGenerator hmg = new HiveMetaDataGenerator(
						this.metadataServiceClient);

				Referenceable dbref = hmg.registerDatabase(t.getDb().getName(),
						this.clustername);
				hivetabref = hmg.registerExtTable(dbref, t.getDb().getName(),
						table_name, clist);
			}

			if (this.genlineage && this.hiveExecflag) {

				/*
				 * loadProcess(String name, String description, String user,
				 * List<Id> inputTables, List<Id> outputTables, String
				 * queryText, String queryPlan, String queryId, String
				 * queryGraph, String... traitNames)
				 */

				loadProcess("mysqlLoader",
						"Lineage creating during ingestion of mysqltables",
						"hive", ImmutableList.of(tabid),
						ImmutableList.of(hivetabref.getId()), "IMPORT", "DATA",
						"DATA", "DATA");

				// Referenceable proc = aec.loadProcess(LOAD_PROCESS_TYPE, );

				// createInstance(proc);

			}

		}

	}

	/**
	 * This method creates and Instance
	 * 
	 * @param referenceable
	 * @return
	 */
	private Id createInstance(Referenceable referenceable) {
		String typeName = referenceable.getTypeName();

		String entityJSON = InstanceSerialization.toJson(referenceable, true);
		System.out.println("Submitting new entity= " + entityJSON);
		JSONObject jsonObject;
		String guid = null;
		try {
			jsonObject = metadataServiceClient.createEntity(entityJSON);

			guid = jsonObject.getString(AtlasClient.GUID);
		} catch (AtlasServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("created instance for type " + typeName + ", guid: "
				+ guid);

		// return the Id for created instance with guid
		return new Id(guid, referenceable.getId().getVersion(),
				referenceable.getTypeName());
	}

	/**
	 * This file creates the database object
	 * 
	 * @param name
	 * @param description
	 * @param owner
	 * @param locationUri
	 * @param traitNames
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * This file creates a rqwStorageDescriptor
	 * 
	 * @param location
	 * @param inputFormat
	 * @param outputFormat
	 * @param compressed
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * 
	 * @param name
	 * @param description
	 * @param user
	 * @param inputTables
	 * @param outputTables
	 * @param queryText
	 * @param queryPlan
	 * @param queryId
	 * @param queryGraph
	 * @param traitNames
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * 
	 * @param name
	 * @param dbId
	 * @param inputTables
	 * @param traitNames
	 * @return
	 * @throws Exception
	 */
	Id view(String name, Id dbId, List<Id> inputTables, String... traitNames)
			throws Exception {
		Referenceable referenceable = new Referenceable(VIEW_TYPE, traitNames);
		referenceable.set("name", name);
		referenceable.set("db", dbId);

		referenceable.set("inputTables", inputTables);

		return createInstance(referenceable);
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void verifyTypesCreated() throws Exception {
		List<String> types = metadataServiceClient.listTypes();
		for (String type : TYPES) {
			assert types.contains(type);
		}
	}

	/**
	 * 
	 * @return
	 */
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
