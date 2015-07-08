package com.atlas.cli;

import java.util.List;

import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.InstanceSerialization;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.atlas.client.AtlasClient;
import com.atlas.client.AtlasEntityConnector;
import com.atlas.client.AtlasEntityCreator;
import com.atlas.client.AtlasEntitySearch;
import com.atlas.client.AtlasTypeDefCreator;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author sdutta
 *
 */
public class AtlasCLI {

	String baseurl;
	Options opt = null;
	AtlasClient aClient = null;
	String action = null;

	{
		System.setProperty("atlas.conf", "/Users/sdutta/Applications/conf");
	}

	public static void main(String[] args) {

		try {
			AtlasCLI cli = new AtlasCLI(args);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param args
	 * @throws ParseException
	 * @throws Exception
	 */
	AtlasCLI(String args[]) throws ParseException {

		CommandLineParser parser = new GnuParser();

		opt = new Options();

		opt.addOption(OptionBuilder
				.withLongOpt(AtlasCLIOptions.action)
				.withDescription(
						"action you want to perform [search|createtype|createSimpleEntity|createDataSet|createProcess|bindProcess]")
				.hasArg().withArgName("action").create()

		);

		opt.addOption(OptionBuilder.withLongOpt(AtlasCLIOptions.url)
				.withDescription("Url for the atlas host http://host:21000")
				.hasArg().withArgName("URL").create()

		);

		opt.addOption(

		OptionBuilder
				.withLongOpt(AtlasCLIOptions.type)
				.withDescription(
						"String describing the type of the object. You can find by querying the list - http://host:21000/api/atlas/types?type=CLASS")
				.hasArg().withArgName("type").create());

		opt.addOption(OptionBuilder.withLongOpt(AtlasCLIOptions.name)
				.withDescription("name of type or entity").hasArg()
				.withArgName("name").create());

		opt.addOption(OptionBuilder.withLongOpt(AtlasCLIOptions.description)
				.withDescription("description of type or entity").hasArg()
				.withArgName("name").create());

		opt.addOption(OptionBuilder.withLongOpt(AtlasCLIOptions.inp_type)
				.withDescription("name of type for input to a lineage")
				.hasArg().withArgName("inp_type").create()

		);
		
		opt.addOption(OptionBuilder.withLongOpt(AtlasCLIOptions.inp_value)
				.withDescription("value for input to a lineage")
				.hasArg().withArgName("inp_value").create()
				);
		
		
		opt.addOption(
				OptionBuilder.withLongOpt(AtlasCLIOptions.out_type)
				.withDescription("name of output to a lineage")
				.hasArg().withArgName("out_type").create()
				);
		
		opt.addOption(
				OptionBuilder.withLongOpt(AtlasCLIOptions.out_value)
				.withDescription("value for output to a lineage")
				.hasArg().withArgName("out_value").create()
				);
		
		

		HelpFormatter formatter = new HelpFormatter();
		
		//formatter.printHelp("action", opt);

		//String[] args1 = new String[] { "-c=search" };

		/*for (int i = 0; i < args.length; i++)
			System.out.println(args[i]);*/

		CommandLine line = parser.parse(opt, args);

		//System.out.println("baseurl" + line.getOptionValue(AtlasCLIOptions.url));
		// line.
		//System.exit(0);
		if (line.hasOption(AtlasCLIOptions.url)) {
			baseurl = line.getOptionValue(AtlasCLIOptions.url);

			//System.out.println("baseurl" + baseurl);
			
			this.aClient = new AtlasClient(baseurl);

		}

		if (line.hasOption(AtlasCLIOptions.action)) {

			this.action = line.getOptionValue(AtlasCLIOptions.action);

			String name = line.getOptionValue(AtlasCLIOptions.name);

			if (AtlasCLIOptions.search.equalsIgnoreCase(this.action)) {
				searchEntities(line);

			} else if (AtlasCLIOptions.createSimpleType.equalsIgnoreCase(this.action)) {

				this.createSimpleType(line);

			} else if (AtlasCLIOptions.createDataSetType.equalsIgnoreCase(this.action)) {

				this.createDataSetType(line);

			} else if (AtlasCLIOptions.createProcessType.equalsIgnoreCase(this.action)) {

				this.createProcessType(line);

			} else if (AtlasCLIOptions.createSimpleEntity
					.equalsIgnoreCase(this.action)) {

				this.createSimpleEntity(line);

			}else if (AtlasCLIOptions.createDataSetEntity
					.equalsIgnoreCase(this.action)) {

				this.createDataSetEntity(line);

			} else if (AtlasCLIOptions.createDataSetEntity
					.equalsIgnoreCase(this.action)) {

				this.createProcessEntity(line);

			} else {
				formatter.printHelp("Usage:", opt);
			}

		}

	}

	/**
	 * This method invoked the AtlasEntity Search
	 * 
	 * @param line
	 */
	private void searchEntities(CommandLine line) {

		try {

			AtlasEntitySearch aES = new AtlasEntitySearch(baseurl);
			String type_name = line.getOptionValue(AtlasCLIOptions.type);
			String value = line.getOptionValue(AtlasCLIOptions.name);
			Referenceable ref = aES.getReferenceByName(type_name, value);

			String entityJSON = InstanceSerialization.toJson(ref, true);

			System.out.println("Search Result:");
			System.out.println(entityJSON);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * 
	 * @param line
	 */
	private void createSimpleType(CommandLine line) {

		try {
			AtlasTypeDefCreator ad = new AtlasTypeDefCreator(baseurl);
			String typename = line.getOptionValue(AtlasCLIOptions.type);
			ad.assembleSimpleType(null, typename);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param typename
	 */
	private void createTraitType(CommandLine line) {

		try {
			AtlasTypeDefCreator ad = new AtlasTypeDefCreator(baseurl);
			String typename = line.getOptionValue(AtlasCLIOptions.type);
			ad.assembleSimpleType(typename, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param processName
	 */
	private void createProcessType(CommandLine line) {
		try {
			AtlasTypeDefCreator ad = new AtlasTypeDefCreator(baseurl);
			String typename = line.getOptionValue(AtlasCLIOptions.type);
			ad.assembleProcessType(null, typename);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param dataSetTypeName
	 */
	private void createDataSetType(CommandLine line) {
		try {
			AtlasTypeDefCreator ad = new AtlasTypeDefCreator(baseurl);
			String typename = line.getOptionValue(AtlasCLIOptions.type);
			ad.assembleDataSetType(null, typename);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param type
	 * @param name
	 * @param description
	 */
	private void createSimpleEntity(CommandLine line) {

		AtlasEntityCreator aec = new AtlasEntityCreator(baseurl);
		String typename = line.getOptionValue(AtlasCLIOptions.type);
		String name = line.getOptionValue(AtlasCLIOptions.name);
		String description = line.getOptionValue(AtlasCLIOptions.description);
		try {
			Referenceable createuniveralEntity = aec.createRefObject(typename,
					name, description);
			aec.createEntity(createuniveralEntity);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 
	 */
	private void createDataSetEntity(CommandLine line) {
		AtlasEntityCreator aec = new AtlasEntityCreator(baseurl);
		try {

			String type = line.getOptionValue(AtlasCLIOptions.type);
			String name = line.getOptionValue(AtlasCLIOptions.name);
			String description = line
					.getOptionValue(AtlasCLIOptions.description);
			String traitnames = line.getOptionValue(AtlasCLIOptions.traitnames);

			// TODO
			// This needs to be replaced by columns

			List<Referenceable> timeDimColumns = ImmutableList.of(
					aec.rawColumn("time_id", "int", "time id"),
					aec.rawColumn("dayOfYear", "int", "day Of Year"),
					aec.rawColumn("weekDay", "int", "week Day"));

			Referenceable referenceable = new Referenceable(type, traitnames);
			referenceable.set("name", name);
			referenceable.set("description", description);
			referenceable.set("createTime", System.currentTimeMillis());
			referenceable.set("lastAccessTime", System.currentTimeMillis());

			referenceable.set("columns", timeDimColumns);

			aec.createEntity(referenceable);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This creates a process Entity
	 * 
	 */
	private void createProcessEntity(CommandLine line) {

		String type = line.getOptionValue(AtlasCLIOptions.type);
		String type_description = line
				.getOptionValue(AtlasCLIOptions.description);

		String inp_type_name = line.getOptionValue(AtlasCLIOptions.inp_type);
		String inp_value = line.getOptionValue(AtlasCLIOptions.inp_value);
		String out_type_name = line.getOptionValue(AtlasCLIOptions.out_type);
		String out_value = line.getOptionValue(AtlasCLIOptions.out_value);
		String traitname = line.getOptionValue(AtlasCLIOptions.traitnames);

		AtlasEntitySearch aES = new AtlasEntitySearch(baseurl);
		AtlasEntityConnector aec = new AtlasEntityConnector();
		Referenceable inpref;
		try {

			inpref = aES.getReferenceByName(inp_type_name, inp_value);

			Referenceable outref = aES.getReferenceByName(out_type_name,
					out_value);

			Referenceable proc = aec.loadProcess(type, type_description,
					ImmutableList.of(inpref.getId()),
					ImmutableList.of(outref.getId()), traitname);

			aec.createEntity(proc);
			System.out.println(" Lineage formed in Atlas");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
