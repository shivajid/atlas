package com.hortonworks.atlas.client;

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
import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;

/*
 * This is a class to create Type Definitions
 * This is a simple class
 * @author - Shivaji Dutta
 */
 
public class AtlasTypeDefCreator {

	{
		System.setProperty("atlas.conf", "conf");
	}

	private String traitName = "Green";

	private AtlasClient ac = null;

	private ImmutableList enumType = null;
	private ImmutableList structType = null;
	private ImmutableList classType = null;
	private ImmutableList traitType = null;

	/**
	 * This assembles the type
	 * If trait name is passed it creates a simple trait with the class name
	 * if not, just the class type is created
	 * 
	 * @return
	 * @throws Exception 
	 */

	public String assembleSimpleType(String traitName, String ClassTypeName, String parenttype) throws Exception {
		

		System.out.print(traitName+  ClassTypeName + parenttype);
		
		TypesDef tdef = TypeUtils.getTypesDef(ImmutableList.<EnumTypeDefinition> of(),
				ImmutableList.<StructTypeDefinition> of(),
				this.createTraitType(traitName),
				this.createClassType(ClassTypeName, parenttype));
		
		

		return TypesSerialization.toJson(tdef);
	}

	/**
	 * This register the Process type
	 * 
	 * @return s
	 */

	public String assembleProcessType(String traitName, String ClassTypeName) {
		TypesDef tdef = null;

		tdef = TypeUtils.getTypesDef(ImmutableList.<EnumTypeDefinition> of(),
				ImmutableList.<StructTypeDefinition> of(),
				this.createTraitType(traitName),
				ImmutableList.of(this.createProcessTypeByName(ClassTypeName)));

		return TypesSerialization.toJson(tdef);
	}

	public String assembleDataSetType(String traitName, String ClassTypeName) {
		TypesDef tdef = null;

		tdef = TypeUtils.getTypesDef(ImmutableList.<EnumTypeDefinition> of(),
				ImmutableList.<StructTypeDefinition> of(),
				this.createTraitType(traitName),
				ImmutableList.of(this.createDataSetTypeByName(ClassTypeName)));
		
		
		

		return TypesSerialization.toJson(tdef);
	}

	/**
	 * 
	 * @param baseurl
	 * @throws AtlasServiceException
	 */
	public AtlasTypeDefCreator(String baseurl) throws AtlasServiceException {

		//System.out.println("Creating Client Connection" + baseurl);
		ac = new AtlasClient(baseurl);
		//System.out.println("Client Object returned");

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

	//public static String Type_GOD = "GOD_Type";
	//public static String Type_Planets = "Planet_Type";
	//public static String Type_Forces = "Force_Type";
	public static String Type_New_Life = "New_Life_Type";
	public static String Type_Asteroids = "Asteroid_Type";
	public static final String COLUMN_TYPE = "Column";

	/**
	 * This returns an ImmutableList of the type being created
	 * 
	 * @param typeName
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public ImmutableList<HierarchicalTypeDefinition<ClassType>> createClassType(
			String typeName, String parenttype) throws Exception {

		if(typeName == null ){
			throw new Exception ("type or typename cannot be null");
		}
		
		System.out.print("class type name" + typeName);
		HierarchicalTypeDefinition<ClassType> genericType =  null;
		if(parenttype != null)
		   {
			genericType = TypesUtil
				.createClassTypeDef(typeName, ImmutableList.of(parenttype),
						attrDef("name", DataTypes.STRING_TYPE),
						attrDef("description", DataTypes.STRING_TYPE),
						attrDef("createTime", DataTypes.INT_TYPE),
						attrDef("lastAccessTime", DataTypes.INT_TYPE)
						
						);
		
		   } else{
			   genericType = TypesUtil
						.createClassTypeDef(typeName, null,
								attrDef("name", DataTypes.STRING_TYPE),
								attrDef("description", DataTypes.STRING_TYPE),
								attrDef("createTime", DataTypes.INT_TYPE),
								attrDef("lastAccessTime", DataTypes.INT_TYPE));
				
		   }
		
		this.classType = ImmutableList.of(genericType);
		return this.classType;
	}

	/**
	 * This lets you create Class Type
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
	 * This method helps you to create types
	 * 
	 * @param typeName
	 * @return
	 */
	public HierarchicalTypeDefinition<ClassType> createProcessTypeByName(
			String typeName) {

		return TypesUtil.createClassTypeDef(typeName,
				ImmutableList.of("Process"),
				attrDef("userName", DataTypes.STRING_TYPE),
				attrDef("startTime", DataTypes.INT_TYPE),
				attrDef("endTime", DataTypes.INT_TYPE));

	}

	/**
	 * 
	 * @param typeName
	 * @return
	 */
	public HierarchicalTypeDefinition<ClassType> createDataSetTypeByName(
			String typeName) {

		return TypesUtil.createClassTypeDef(
				typeName,
				ImmutableList.of("DataSet"),
				attrDef("createTime", DataTypes.INT_TYPE),
				attrDef("lastAccessTime", DataTypes.INT_TYPE),
				new AttributeDefinition("columns", DataTypes
						.arrayTypeName(COLUMN_TYPE), Multiplicity.COLLECTION,
						true, null));

	}

	/**
	 * This method helps for creating traits
	 * 
	 * @param trait
	 * @return
	 */
	public ImmutableList<HierarchicalTypeDefinition<TraitType>> createTraitType(
			String trait) {

		if (trait != null)
			return ImmutableList.of(TypesUtil.createTraitTypeDef(
					trait, null));
		else
			return ImmutableList
					.<HierarchicalTypeDefinition<TraitType>> of();

		
	}
	
	

	/**
	 * 
	 * @param args
	 * @throws Exception
	 
	public static void main(String[] args) throws Exception {

		if (args.length < 0)
			throw new Exception("Please pass the atlas base url");

		String baseurl = args[0];

		System.out.println(" Baseurl" + baseurl);
		AtlasTypeDefCreator ad = new AtlasTypeDefCreator(baseurl);
		ad.traitName = args[1];
		// ad.registerTypes();

	}
	
	*/

	/**
	 * This is for registering types
	 * 
	 * 
	 * public void registerTypes() throws org.apache.atlas.AtlasServiceException
	 * {
	 * 
	 * System.out.println("Registering Types"); ac.createType(assembleTypes());
	 * System.out.println("Done Creating Types"); }
	 */

	/**
	 * This is a sample code with hard coded values
	 * 
	 * @deprecated
	 * @return
	 * 
	 *         ImmutableList<HierarchicalTypeDefinition<ClassType>>
	 *         createClassTypes() {
	 * 
	 *         HierarchicalTypeDefinition<ClassType> UniversalTypes =
	 * 
	 *         TypesUtil.createClassTypeDef(this.Type_GOD, null, attrDef("name",
	 *         DataTypes.STRING_TYPE), attrDef("description",
	 *         DataTypes.STRING_TYPE));
	 * 
	 *         HierarchicalTypeDefinition<ClassType> GeneralTypes =
	 * 
	 *         TypesUtil.createClassTypeDef(this.Type_Planets, null,
	 *         attrDef("name", DataTypes.STRING_TYPE), attrDef("description",
	 *         DataTypes.STRING_TYPE));
	 * 
	 *         HierarchicalTypeDefinition<ClassType> ConnectorTypes =
	 * 
	 *         TypesUtil.createClassTypeDef(this.Type_Forces, null,
	 *         attrDef("name", DataTypes.STRING_TYPE), attrDef("description",
	 *         DataTypes.STRING_TYPE));
	 * 
	 *         HierarchicalTypeDefinition<ClassType> processtype = this
	 *         .createProcessTypes();
	 * 
	 * 
	 *         HierarchicalTypeDefinition<ClassType> columnClsDef = TypesUtil
	 *         .createClassTypeDef(COLUMN_TYPE, null, attrDef("name",
	 *         DataTypes.STRING_TYPE), attrDef("dataType",
	 *         DataTypes.STRING_TYPE), attrDef("comment",
	 *         DataTypes.STRING_TYPE));
	 * 
	 *         HierarchicalTypeDefinition<ClassType> asteroidDef = TypesUtil
	 *         .createClassTypeDef( this.Type_Asteroids,
	 *         ImmutableList.of("DataSet"), attrDef("createTime",
	 *         DataTypes.INT_TYPE), attrDef("lastAccessTime",
	 *         DataTypes.INT_TYPE), attrDef("speed", DataTypes.INT_TYPE),
	 *         attrDef("distance_frm_Earth", DataTypes.STRING_TYPE), new
	 *         AttributeDefinition("columns", DataTypes
	 *         .arrayTypeName(COLUMN_TYPE), Multiplicity.COLLECTION, true,
	 *         null));
	 * 
	 *         return // ImmutableList.of(UniversalTypes, GeneralTypes,
	 *         ConnectorTypes); ImmutableList.of(columnClsDef, asteroidDef);
	 * 
	 *         }
	 
	 
	 * The JSON Type String for the TypeDef
	 * 
	 * public String assembleTypes() {
	 * 
	 * TypesDef tdef = TypeUtils.getTypesDef( ImmutableList.<EnumTypeDefinition>
	 * of(), ImmutableList.<StructTypeDefinition> of(),
	 * this.createTraitType(traitName), this.createClassTypes());
	 * 
	 * return TypesSerialization.toJson(tdef);
	 * 
	 * }
	 */

}
