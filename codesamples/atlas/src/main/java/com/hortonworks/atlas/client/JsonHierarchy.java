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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import com.hortonworks.atlas.adapter.EntityModel;
import com.hortonworks.atlas.adapter.TupleModel;

/** 
 * @author sdutta
 *
 */
public class JsonHierarchy {

	public static String path = null;

	JsonParser jParser;
	private int i = 0;
	JsonToken jtk = null;

	ArrayList<TupleModel> tmapList = new ArrayList<TupleModel>();

	ArrayList<TupleModel> rotatingList = new ArrayList<TupleModel>();

	boolean hitarray = false;
	int hitarray_index = 0;
	private ArrayList<EntityModel> emList = new ArrayList<EntityModel>();
	EntityModel EM = null;




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
	 * 
	 * @param filepath
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public void parseJSON(String filepath) throws JsonParseException,
			IOException {

		JsonFactory jfac = new JsonFactory();
		jParser = jfac.createJsonParser(new File(filepath));

		buildTrees(jParser, null, null);

		ListIterator<TupleModel> tmI = tmapList.listIterator();
		TupleModel tm = null;
		String header = "Trait Name" + "   --   " + "Parent Trait"
				+ "    --   " + "Level";
		
		for(int i = 0; i < header.length(); i++){
			System.out.print("#");
		}
		System.out.println();
		
		System.out.println(header);

		for(int i = 0; i < header.length(); i++){
			System.out.print("#");
		}
		System.out.println();
		
		while (tmI.hasNext()) {

			tm = tmI.next();
			System.out.println(tm.getCurrnode() + "   --   " + tm.getParentnode()
					+ "   --   " + tm.getLevel());
		}

		System.out.println("Printing entities");
		ListIterator<EntityModel> emItr = this.emList.listIterator();
		EntityModel em1 = null;

		String header2 = "Entity Type" + "   --   " + "Entity Name"
				+ "    --   " + " Trait Name";
		
		
		for(int i = 0; i < header2.length(); i++){
			System.out.print("#");
		}
		System.out.println();
		
		System.out.println(header2);

		for(int i = 0; i < header2.length(); i++){
			System.out.print("#");
		}
		System.out.println();
		
		
		
		while (emItr.hasNext()) {

			em1 = emItr.next();

			System.out.println(em1.getType() + "   --   " + em1.getName() + "   --   "
					+ em1.getParent());
		}

	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<EntityModel> getEmList() {
		return emList;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<TupleModel> getTmapList() {
		return tmapList;
	}

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

			if (hitarray && jParser.getText() == "{") {
				EM = new EntityModel();
				EM.setParent(parentnode);
			}

			if (hitarray && jParser.getText() == "type") {
				currnode = "type";
				jtk = jParser.nextToken();
				if (jtk.isScalarValue()) {
					EM.setType(jParser.getText());
				}

			}

			if (hitarray && jParser.getText() == "name") {
				currnode = "name";
				jtk = jParser.nextToken();
				if (jtk.isScalarValue()) {
					EM.setName(jParser.getText());
				}

			}

			if (hitarray && jParser.getText() == "}") {
				this.emList.add(EM);
				EM = null;
			}

			if (jParser.getText() == "[") {

				// parentnode = currnode;
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
	 * 
	 * @param jParser
	 * @param parentnode
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public void processEntities(JsonParser jParser, String parentnode,
			EntityModel em) throws JsonParseException, IOException {

		jtk = jParser.getCurrentToken();
		if (jtk == JsonToken.FIELD_NAME) {

			String currnode = jParser.getCurrentName();

			em.setParent(parentnode);

			jParser.nextToken();

			if (jtk.isScalarValue()) {

				if ("type".equalsIgnoreCase(currnode))
					em.setType(currnode);
				else if ("name".equalsIgnoreCase(currnode))
					em.setName(currnode);

			}
		}

		if (jParser.getText() == "[") {

			jParser.nextToken();
			processEntities(jParser, parentnode, em);

		}

		if (jParser.getText() == "{") {

			jParser.nextToken();
			EM = new EntityModel();
			processEntities(jParser, parentnode, EM);

		}

		if (jParser.getText() == "}") {

			jParser.nextToken();
			emList.add(em);
			processEntities(jParser, parentnode, EM);

		}

		if (jParser.getText() == "[") {

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
	private void pushTuple(TupleModel tupMd) {

		this.rotatingList.add(tupMd);
	}

	/**
	 * pop the last entry out
	 * 
	 */
	private void popTupleModel() {

		this.rotatingList.remove(this.rotatingList.size() - 1);
	}

}
