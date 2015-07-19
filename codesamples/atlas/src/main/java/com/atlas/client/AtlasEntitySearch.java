package com.atlas.client;

import java.util.List;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.apache.atlas.typesystem.persistence.Id;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



/**
 * This code shows an example of searching for Entities by name
 * @author sdutta
 *
 */
public class AtlasEntitySearch {

	{
		System.setProperty("atlas.conf", "./conf");
	}

	private AtlasClient ac = null;


	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 0)
			throw new Exception("Please pass the atlas base url");

		String baseurl = args[0];
		String type_name = args[1];
		String value = args[2];

		System.out.println(baseurl);
		System.out.println(type_name);
		System.out.println(value);

		System.out.println(" Baseurl" + baseurl);
		AtlasEntitySearch aES = new AtlasEntitySearch(baseurl);

		try {

			Referenceable ref = aES.getReferenceByName(type_name, value);

			String entityJSON = InstanceSerialization.toJson(ref, true);

			System.out.println(entityJSON);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// aEC.destroy();

	}

	/**
	 * 
	 * @param baseurl
	 */
	public AtlasEntitySearch(String baseurl) {

		System.out.println("Creating Client Connection" + baseurl);
		ac = new AtlasClient(baseurl);
		System.out.println("Client Object returned");

	}

	/**
	 * 
	 * @param typeName
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Referenceable getReferenceByName(String typeName, String value)
			throws Exception {

		System.out.println(String.format("Getting reference for Entity %s",
				value));

		String dslQuery = String.format("%s where %s = '%s'", typeName, "name",
				value);

		return getEntityReferenceFromDSL(typeName, dslQuery);
	}

	/*
	 * This will get an entity Object
	 */
	private Referenceable getEntityReferenceFromDSL(String typeName,
			String dslQuery) throws Exception {

		AtlasClient dgiClient = ac;

		JSONArray results = dgiClient.searchByDSL(dslQuery);
		if (results.length() == 0) {
			return null;
		} else {
			String guid;
			JSONObject row = results.getJSONObject(0);

			if (row.has("$id$")) {
				guid = row.getJSONObject("$id$").getString("id");
			} else {
				guid = row.getJSONObject("_col_0").getString("id");
			}

			return new Referenceable(guid, typeName, null);
		}
	}

	/*
	 * 
	 */
	public AtlasClient getAtlasClient() {

		return ac;
	}

}
