/**
 * 
 */
package com.hortonworks.atlas;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;

/**
 * @author sdutta
 * @param <T>
 *
 */
public class JsonHierarchy<T> implements Hierarchy<T> {

	public static String path = null;
	private File f = null;

	public static void main(String[] args) throws JsonParseException,
			IOException {

		path = "/Users/sdutta/DGI/codesamples/atlas/VehicleOrg.json";
		JsonHierarchy jsn = new JsonHierarchy(path);

	}

	
	/**
	 * @throws IOException
	 * @throws JsonParseException
	 * 
	 */
	public JsonHierarchy(String filepath) throws JsonParseException,
			IOException {

		JsonFactory jfac = new JsonFactory();
		JsonParser jParser = jfac.createJsonParser(new File(filepath));

		JsonToken jtk = jParser.getCurrentToken();
		jtk = jParser.nextToken();
		String parent = "root";
		String current = "dum";

		int i = 0;

		while (jParser.hasCurrentToken()) {

			if (jtk == JsonToken.FIELD_NAME) {

				System.out.print("name: " + i + " : "
						+ jParser.getCurrentName());

				current = jParser.getCurrentName(); // update the current field
													// name

				jtk = jParser.nextToken(); // looking for value

				// if(i > 0) parent = current;

				if (jtk == JsonToken.START_ARRAY) {

					jtk = jParser.nextToken();

					while (jtk != JsonToken.END_ARRAY) {

						if (jtk == JsonToken.FIELD_NAME) {
							System.out.print("name: " + i + " : "
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

	}

	public void parse() {
		// TODO Auto-generated method stub

	}

}
