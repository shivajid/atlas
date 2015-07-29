/**
 * 
 */
package com.hortonworks.atlas;

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

import com.hortonworks.atlas.adapter.TupleModel;

/**
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

	public void parse(String filepath) throws JsonParseException, IOException {

		// TODO Auto-generated method stub

		JsonFactory jfac = new JsonFactory();
		jParser = jfac.createJsonParser(new File(filepath));
		demoList = new ArrayList<String>();

		JsonToken jtk = jParser.getCurrentToken();
		jtk = jParser.nextToken();

		int i = 0;
		String fieldname = null;

		while (jParser.hasCurrentToken()) {

			if (jtk == JsonToken.FIELD_NAME) {

				System.out.print("name: " + i + " : "
						+ jParser.getCurrentName());

				demoList.add("name:" + i + "-" + jParser.getCurrentName());

				jtk = jParser.nextToken(); // looking for value

				if (jtk == JsonToken.START_ARRAY) {

					jtk = jParser.nextToken();

					while (jtk != JsonToken.END_ARRAY) {

						if (jtk == JsonToken.FIELD_NAME) {

							System.out.print("name: " + i + " : "
									+ jParser.getCurrentName());
							demoList.add("name:" + i + "-"
									+ jParser.getCurrentName());

							jtk = jParser.nextToken();
						}

						if (jtk.isScalarValue()) {

							System.out.println(" === " + jParser.getText());

						} else
							System.out.println();

						if (jParser.getText() == "{") {

							i++;

						} else if (jParser.getText() == "}") {
							i--;

						}

						jtk = jParser.nextToken();
					}

					// System.out.println(" ::: " + jParser.getText());
				} else if (jtk.isScalarValue()) {

					System.out.println(" ::: " + jParser.getText());
				} else {

					System.out.println(" --ignore--  " + jParser.getText());
				}
			}

			if (jParser.getText() == "{") {

				i++;

			} else if (jParser.getText() == "}") {
				i--;

			}

			jtk = jParser.nextToken();
			// System.out.println(" ::: " + jParser.getText());
		}

		// Collections.sort(demoList);
		String parent = null;
		String level = null;
		String current = null;
		int l, p = 0;
		for (int j = 0; j < demoList.size(); j++) {

			StringTokenizer stk = new StringTokenizer(demoList.get(j), "-");
			level = stk.nextToken();
			current = stk.nextToken();
			String num = (String) level.subSequence(5, level.length())
					.toString();

			l = Integer.valueOf(num).intValue();

			// System.out.println(l);

			if (l > p) {

				parent = current;
				System.out.println(current + "-" + parent);
				p = l;

			} else
				System.out.println(current + "-" + parent);
			;

		}

	}

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

			System.out.println(tm.getCurrnode() + "--" + tm.getParentnode() + " -- " + tm.getLevel());
		}

	}

	private int i = 0;
	JsonToken jtk = null;
	
	ArrayList<TupleModel> tmapList = new ArrayList<TupleModel>();

	ArrayList<TupleModel> rotatingList = new ArrayList<TupleModel>();

	
	int reversenodecount = 0;
	boolean hitarray = false;
	int hitarray_index = 0;

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

				// System.out.println("Current Node: " + i + " : " + currnode
				// + "| Parent Node " + parentnode);
				//
				currnode = jParser.getCurrentName();

				
	

				TupleModel tm = new TupleModel(currnode, parentnode);
				tm.setLevel(i);
				tmapList.add(tm);
				this.pushTuple(tm);
				
				int length = tmapList.size();
				System.out.println(currnode + "--" + parentnode + " -- " + i);
				jtk = jParser.nextToken();

			

			}
			
			
			if (jParser.getText() == "[") {

				//i++;
				parentnode = currnode;
				hitarray = true;
				hitarray_index++;

			
			} else if (jParser.getText() == "]") {
				//i--;
				//reversenodecount++;
				
				hitarray_index--;
				if(hitarray_index == 0)
					hitarray = false;

			}
			

			if (jParser.getText() == "{" && !hitarray) {

				i++;
				
				parentnode = currnode;

			} else if (jParser.getText() == "}" && !hitarray) {
				i--;
			//	reversenodecount++;
				
				this.popTupleModel();
				
				TupleModel tm = this.rotatingList.get(this.rotatingList.size() -1);
				parentnode =  tm.getParentnode();
				
			}

			buildTrees(jParser, currnode, parentnode);

		}

	}
	
	public void pushTuple(TupleModel tupMd){
		
		this.rotatingList.add(tupMd);
	}
	
	public void popTupleModel(){
		
		this.rotatingList.remove(this.rotatingList.size() -1);
	}
	
	

}
