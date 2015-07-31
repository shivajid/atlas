/**
 *
 * 
 */
package com.atlas.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;

import com.hortonworks.atlas.adapter.EntityModel;
import com.hortonworks.atlas.adapter.TupleModel;

/**
 * 
 * 
 * 
 * @author sdutta
 * @param <T>
 *
 */
public class JsonHierarchy {

	public static String path = null;
	private File f = null;

	private HashMap<String, String> parChTMap = null;
	private HashMap<String, String> entTMap = null;
	private ArrayList<String> demoList = null;
	JsonParser jParser;

	/**
	 * 
	 * @param args
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws JsonParseException,
			IOException {

		path = "/Users/sdutta/DGI/codesamples/atlas/TestHierarchy.json";
		JsonHierarchy jsn = new JsonHierarchy();
		jsn.parseJSON(path);

	}

	/**
	 * @throws IOException
	 * @throws JsonParseException
	 * 
	 */
	public JsonHierarchy() throws JsonParseException, IOException {

	}

	/**
	 * 
	 * @param filepath
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public void parseJSON(String filepath) throws JsonParseException,
			IOException {

		JsonFactory jfac = new JsonFactory();
		jParser = jfac.createJsonParser(new File(filepath));
		demoList = new ArrayList<String>();

		buildTrees(jParser, null, null);

		ListIterator<TupleModel> tmI = tmapList.listIterator();
		TupleModel tm = null;

		while (tmI.hasNext()) {

			tm = tmI.next();

			System.out.println(tm.getCurrnode() + "--" + tm.getParentnode()
					+ " -- " + tm.getLevel());
		}
		
		System.out.println("Printing entities");
		ListIterator<EntityModel> emItr = this.emList.listIterator();
		EntityModel em1 =  null;
		
		while (emItr.hasNext()) {

			em1 = emItr.next();

			System.out.println(em1.getType() + "--" + em1.getName()
					+ " -- " + em1.getParent());
		}
		
	}

	private int i = 0;
	JsonToken jtk = null;

	ArrayList<TupleModel> tmapList = new ArrayList<TupleModel>();

	ArrayList<TupleModel> rotatingList = new ArrayList<TupleModel>();

	// int reversenodecount = 0;
	boolean hitarray = false;
	int hitarray_index = 0;
	private ArrayList<EntityModel> emList = new ArrayList<EntityModel>();
	EntityModel EM = null;
	

	
	
	/**
	 * 
	 * use push and pop method to get to the parent
	 * 
	 * @param node
	 * @param pnode
	 * @throws IOException
	 * @throws JsonParseException
	 */
	public void buildTrees(JsonParser jParser, String currnode,
			String parentnode) throws JsonParseException, IOException {

		jtk = jParser.nextToken();

		if (jParser.hasCurrentToken()) {


			if (jtk == JsonToken.FIELD_NAME && !hitarray) {

				currnode = jParser.getCurrentName();

			
				currnode = jParser.getCurrentName();
				

				TupleModel tm = new TupleModel(currnode, parentnode);
				tm.setLevel(i);
				tmapList.add(tm);
				this.pushTuple(tm);
				jtk = jParser.nextToken();

			} 
			
			if(hitarray && jParser.getText() == "{" ){
				EM = new EntityModel();
				EM.setParent(parentnode);
			}
			
			if(hitarray && jParser.getText() == "type" ){
				currnode = "type";
				jtk = jParser.nextToken();
				if(jtk.isScalarValue()){
					EM.setType(jParser.getText());
				}
				
			}
			
			if(hitarray && jParser.getText() == "name" ){
				currnode = "name";
				jtk = jParser.nextToken();
				if(jtk.isScalarValue()){
					EM.setName(jParser.getText());
				}
				
			}
			
			if(hitarray && jParser.getText() == "}" ){
				this.emList.add(EM);
				EM = null;
			}

			
			if (jParser.getText() == "[") {

				//parentnode = currnode;
				hitarray = true;
				hitarray_index++;

			} else if (jParser.getText() == "]") {
			
				hitarray_index--;
				if (hitarray_index == 0)
					hitarray = false;

			}

			
			
			if (jParser.getText() == "{" && !hitarray) {

				i++;

				parentnode = currnode;

			} else if (jParser.getText() == "}" && !hitarray) {
			
				this.popTupleModel();

				TupleModel tm = this.rotatingList
						.get(this.rotatingList.size() - 1);
				parentnode = tm.getParentnode();

			}

			buildTrees(jParser, currnode, parentnode);

		}

	}

		
	/**
	 * This is to handle the Entities
	 * @param jParser
	 * @param parentnode
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public void processEntities(JsonParser jParser, String parentnode, EntityModel em)
			throws JsonParseException, IOException {

		jtk = jParser.getCurrentToken();
		if (jtk == JsonToken.FIELD_NAME) {

			String currnode = jParser.getCurrentName();
			
			em.setParent(parentnode);
			
			jParser.nextToken();

			if (jtk.isScalarValue()) {
				
				if("type".equalsIgnoreCase(currnode) )
					em.setType(currnode);
				else if ("name".equalsIgnoreCase(currnode))
					em.setName(currnode);
				
				
			}
		}

		if (jParser.getText() == "[" ) {

			jParser.nextToken();
			processEntities(jParser, parentnode, em);

		}
		
		if (jParser.getText() == "{" ) {

			jParser.nextToken();
			 EM = new EntityModel();
			processEntities(jParser, parentnode, EM);

		}
		
		if (jParser.getText() == "}" ) {

			jParser.nextToken();
			emList.add(em);
			processEntities(jParser, parentnode, EM);

		}

		if (jParser.getText() == "[" ) {

			jParser.nextToken();
			processEntities(jParser, parentnode, em);

		}


		if (jParser.getText() == "]") {
			
		}

	}

	/**
	 * 
	 * push the last entry in
	 * 
	 * @param tupMd
	 */
	public void pushTuple(TupleModel tupMd) {

		this.rotatingList.add(tupMd);
	}

	/**
	 * pop the last entry out
	 * 
	 */
	public void popTupleModel() {

		this.rotatingList.remove(this.rotatingList.size() - 1);
	}

	/**
	 * @deprecated
	 * @param filepath
	 * @throws JsonParseException
	 * @throws IOException
	 */

	/**
	 * public void parse(String filepath) throws JsonParseException, IOException
	 * {
	 * 
	 * // TODO Auto-generated method stub
	 * 
	 * JsonFactory jfac = new JsonFactory(); jParser = jfac.createJsonParser(new
	 * File(filepath)); demoList = new ArrayList<String>();
	 * 
	 * JsonToken jtk = jParser.getCurrentToken(); jtk = jParser.nextToken();
	 * 
	 * int i = 0; String fieldname = null;
	 * 
	 * while (jParser.hasCurrentToken()) {
	 * 
	 * if (jtk == JsonToken.FIELD_NAME) {
	 * 
	 * System.out.print("name: " + i + " : " + jParser.getCurrentName());
	 * 
	 * demoList.add("name:" + i + "-" + jParser.getCurrentName());
	 * 
	 * jtk = jParser.nextToken(); // looking for value
	 * 
	 * if (jtk == JsonToken.START_ARRAY) {
	 * 
	 * jtk = jParser.nextToken();
	 * 
	 * while (jtk != JsonToken.END_ARRAY) {
	 * 
	 * if (jtk == JsonToken.FIELD_NAME) {
	 * 
	 * System.out.print("name: " + i + " : " + jParser.getCurrentName());
	 * demoList.add("name:" + i + "-" + jParser.getCurrentName());
	 * 
	 * jtk = jParser.nextToken(); }
	 * 
	 * if (jtk.isScalarValue()) {
	 * 
	 * System.out.println(" === " + jParser.getText());
	 * 
	 * } else System.out.println();
	 * 
	 * if (jParser.getText() == "{") {
	 * 
	 * i++;
	 * 
	 * } else if (jParser.getText() == "}") { i--;
	 * 
	 * }
	 * 
	 * jtk = jParser.nextToken(); }
	 * 
	 * // System.out.println(" ::: " + jParser.getText()); } else if
	 * (jtk.isScalarValue()) {
	 * 
	 * System.out.println(" ::: " + jParser.getText()); } else {
	 * 
	 * System.out.println(" --ignore--  " + jParser.getText()); } }
	 * 
	 * if (jParser.getText() == "{") {
	 * 
	 * i++;
	 * 
	 * } else if (jParser.getText() == "}") { i--;
	 * 
	 * }
	 * 
	 * jtk = jParser.nextToken(); // System.out.println(" ::: " +
	 * jParser.getText()); }
	 * 
	 * // Collections.sort(demoList); String parent = null; String level = null;
	 * String current = null; int l, p = 0; for (int j = 0; j < demoList.size();
	 * j++) {
	 * 
	 * StringTokenizer stk = new StringTokenizer(demoList.get(j), "-"); level =
	 * stk.nextToken(); current = stk.nextToken(); String num = (String)
	 * level.subSequence(5, level.length()) .toString();
	 * 
	 * l = Integer.valueOf(num).intValue();
	 * 
	 * // System.out.println(l);
	 * 
	 * if (l > p) {
	 * 
	 * parent = current; System.out.println(current + "-" + parent); p = l;
	 * 
	 * } else System.out.println(current + "-" + parent); ;
	 * 
	 * }
	 * 
	 * }
	 */

}
