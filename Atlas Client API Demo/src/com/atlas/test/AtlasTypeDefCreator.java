package com.atlas.test;

import java.util.List;
import java.util.ListIterator;
import org.apache.atlas.AtlasClient;
import org.apache.atlas.typesystem.TypesDef;
import org.apache.atlas.typesystem.json.TypesSerialization;
import org.apache.atlas.typesystem.types.AttributeDefinition;
import org.apache.atlas.typesystem.types.ClassType;
import org.apache.atlas.typesystem.types.DataTypes;
import org.apache.atlas.typesystem.types.EnumTypeDefinition;
import org.apache.atlas.typesystem.types.HierarchicalTypeDefinition;
import org.apache.atlas.typesystem.types.IDataType;
import org.apache.atlas.typesystem.types.Multiplicity;
import org.apache.atlas.typesystem.types.StructTypeDefinition;
import org.apache.atlas.typesystem.types.TraitType;
import org.apache.atlas.typesystem.types.TypeUtils;
import org.apache.atlas.typesystem.types.utils.TypesUtil;
import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/*
 * 
 * 
 */
public class AtlasTypeDefCreator {

	{
		System.setProperty("atlas.conf", "/Users/sdutta/Applications/conf");
	}

	private String traitName = "Green";

	private AtlasClient ac = null;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		if (args.length < 0)
			throw new Exception("Please pass the atlas base url");

		String baseurl = args[0];

		System.out.println(" Baseurl" + baseurl);
		AtlasTypeDefCreator ad = new AtlasTypeDefCreator(baseurl);
		ad.traitName = args[1];
		ad.registerTypes();

	}

	public void registerTypes() throws org.apache.atlas.AtlasServiceException {

		System.out.println("Registering Types");
		ac.createType(assembleTypes());
		System.out.println("Done Creating Types");
	}

	/*
	 * The JSON Type String for the TypeDef
	 */
	public String assembleTypes() {

		TypesDef tdef = TypeUtils.getTypesDef(
				ImmutableList.<EnumTypeDefinition> of(),
				ImmutableList.<StructTypeDefinition> of(),
				this.createTraitType(traitName), this.createClassTypes());
		return TypesSerialization.toJson(tdef);

	}

	/**
	 * 
	 * @param baseurl
	 * @throws AtlasServiceException
	 */
	public AtlasTypeDefCreator(String baseurl) throws AtlasServiceException {

		System.out.println("Creating Client Connection" + baseurl);
		ac = new AtlasClient(baseurl);
		System.out.println("Client Object returned");

	}

	AttributeDefinition attrDef(String name, IDataType dT, Multiplicity m,
			boolean isComposite, String reverseAttributeName) {
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(dT);
		return new AttributeDefinition(name, dT.getName(), m, isComposite,
				reverseAttributeName);
	}

	AttributeDefinition attrDef(String name, IDataType dT) {
		return attrDef(name, dT, Multiplicity.OPTIONAL, false, null);
	}

	AttributeDefinition attrDef(String name, IDataType dT, Multiplicity m) {
		return attrDef(name, dT, m, false, null);
	}

	public static String Type_GOD = "GOD_Type";
	public static String Type_Planets = "Planet_Type";
	public static String Type_Forces = "Force_Type";
	public static String Type_New_Life = "New_Life_Type";
	public static String Type_Asteroids = "Asteroid_Type";
	public static final String COLUMN_TYPE = "Column";

	
	
	
	
	/**
	 * 
	 * @return
	 */
	public ImmutableList<HierarchicalTypeDefinition<ClassType>> createClassTypes() {

		HierarchicalTypeDefinition<ClassType> UniversalTypes =

		TypesUtil.createClassTypeDef(this.Type_GOD, null,
				attrDef("name", DataTypes.STRING_TYPE),
				attrDef("description", DataTypes.STRING_TYPE));

		HierarchicalTypeDefinition<ClassType> GeneralTypes =

		TypesUtil.createClassTypeDef(this.Type_Planets, null,
				attrDef("name", DataTypes.STRING_TYPE),
				attrDef("description", DataTypes.STRING_TYPE));

		HierarchicalTypeDefinition<ClassType> ConnectorTypes =

		TypesUtil.createClassTypeDef(this.Type_Forces, null,
				attrDef("name", DataTypes.STRING_TYPE),
				attrDef("description", DataTypes.STRING_TYPE));

		HierarchicalTypeDefinition<ClassType> processtype = this
				.createProcessTypes();

		/**
		 * Creating Type Data Set to join 2 types
		 */
		HierarchicalTypeDefinition<ClassType> columnClsDef = TypesUtil
				.createClassTypeDef(COLUMN_TYPE, null,
						attrDef("name", DataTypes.STRING_TYPE),
						attrDef("dataType", DataTypes.STRING_TYPE),
						attrDef("comment", DataTypes.STRING_TYPE));

		HierarchicalTypeDefinition<ClassType> asteroidDef = TypesUtil
				.createClassTypeDef(
						this.Type_Asteroids,
						ImmutableList.of("DataSet"),
						attrDef("createTime", DataTypes.INT_TYPE),
						attrDef("lastAccessTime", DataTypes.INT_TYPE),
						attrDef("speed", DataTypes.INT_TYPE),
						attrDef("distance_frm_Earth", DataTypes.STRING_TYPE),
						new AttributeDefinition("columns", DataTypes
								.arrayTypeName(COLUMN_TYPE),
								Multiplicity.COLLECTION, true, null));

		return
		// ImmutableList.of(UniversalTypes, GeneralTypes, ConnectorTypes);
		ImmutableList.of(columnClsDef, asteroidDef);

	}

	/**
	 * 
	 * @return
	 */
	public HierarchicalTypeDefinition<ClassType> createProcessTypes() {

		return TypesUtil.createClassTypeDef(this.Type_New_Life,
				ImmutableList.of("Process"),
				attrDef("userName", DataTypes.STRING_TYPE),
				attrDef("startTime", DataTypes.INT_TYPE),
				attrDef("endTime", DataTypes.INT_TYPE));

	}

	/**
	 * 
	 * @param trait
	 * @return
	 */
	public ImmutableList<HierarchicalTypeDefinition<TraitType>> createTraitType(
			String trait) {

		return ImmutableList.of(TypesUtil.createTraitTypeDef(trait, null));

	}

}
