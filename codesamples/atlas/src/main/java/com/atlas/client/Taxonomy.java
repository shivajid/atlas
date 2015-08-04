package com.atlas.client;

import java.util.ArrayList;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.typesystem.Referenceable;
import org.apache.atlas.typesystem.json.TypesSerialization;
import org.apache.atlas.typesystem.types.AttributeDefinition;
import org.apache.atlas.typesystem.types.ClassType;
import org.apache.atlas.typesystem.types.DataTypes;
import org.apache.atlas.typesystem.types.EnumTypeDefinition;
import org.apache.atlas.typesystem.types.HierarchicalTypeDefinition;
import org.apache.atlas.typesystem.types.IDataType;
import org.apache.atlas.typesystem.types.Multiplicity;
import org.apache.atlas.typesystem.types.StructTypeDefinition;
import org.apache.atlas.typesystem.types.TypeUtils;
import org.apache.atlas.typesystem.types.utils.TypesUtil;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class Taxonomy {
	{
		System.setProperty("atlas.conf", "conf");
	}
	
	@SuppressWarnings("unused")
	private  AtlasClient ac = null;
	
	
	
	/**
	 * This creates an instance of the taxonomy class
	 * It will create a taxonomy using the traits
	 * Constructor
	 * @param baseurl
	 */
	
	public Taxonomy(String baseurl, String traitname, String Supertrait) {
		
		ac = new AtlasClient(baseurl);
		try {
			ac.createType(this.createTraitTypes(traitname, Supertrait));
		
		
		} catch (AtlasServiceException e) {
			
			e.printStackTrace();
		}
	
	
	}
	
	public Taxonomy() {
	
	
	}

	
	
	
	/**
	 * This is a generic method to create types
	 * 
	 */
	public String createTraitTypes(String traitname, String supertrait){
		
		
		//System.out.println("Supertrait: " + supertrait);
				
				if (supertrait == null ){
					return  TypesSerialization.toJson(TypeUtils.getTypesDef(
				ImmutableList.<EnumTypeDefinition> of(),
				ImmutableList.<StructTypeDefinition> of(),
		ImmutableList.of(TypesUtil.createTraitTypeDef(traitname, null )),
		ImmutableList.<HierarchicalTypeDefinition<ClassType>>of()));
				}else
				{
					return  TypesSerialization.toJson(TypeUtils.getTypesDef(
							ImmutableList.<EnumTypeDefinition> of(),
							ImmutableList.<StructTypeDefinition> of(),
					ImmutableList.of(TypesUtil.createTraitTypeDef(traitname, ImmutableList.of(supertrait) )),
					ImmutableList.<HierarchicalTypeDefinition<ClassType>>of()));
					
				}
		
		
	}
	

	
	
	
	/**
	 * 
	 * @param name
	 * @param dT
	 * @param m
	 * @param isComposite
	 * @param reverseAttributeName
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	AttributeDefinition attrDef(String name, IDataType dT, Multiplicity m,
			boolean isComposite, String reverseAttributeName) {
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(dT);
		return new AttributeDefinition(name, dT.getName(), m, isComposite,
				reverseAttributeName);
	}

	/**
	 * 
	 * @param name
	 * @param dT
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	AttributeDefinition attrDef(String name, IDataType dT) {
		return attrDef(name, dT, Multiplicity.OPTIONAL, false, null);
	}

	@SuppressWarnings("rawtypes")
	AttributeDefinition attrDef(String name, IDataType dT, Multiplicity m) {
		return attrDef(name, dT, m, false, null);
	}
	

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length < 2)
			throw new Exception("Please pass the atlas base url and the typename");
		
		String traitname  = null;
		String baseurl = args[0];
		
		String[] arr = null;
		
		
		if(args[1] != null)
		 traitname = args[1];
		else
			throw new Exception("Please pass the traitname");
		
		//ArrayList alist = null;
		
		Taxonomy tx = null;
		if(args.length > 2 )
		{
			
			//alist = new ArrayList<String>();
					
			String supertrait = args[2];
			
			tx = new Taxonomy(baseurl, traitname, supertrait);
			
			
		}else
			 tx = new Taxonomy(baseurl, traitname, null);
		
		
		
		
		
		System.out.println("Done creating trait " + traitname);
		
		
		
		/*AtlasEntityCreator aec = new AtlasEntityCreator(baseurl);
		
		    Referenceable referenceable = new Referenceable("DB", "SuperGreen3");
	        referenceable.set("name",  "SuperGreen3Entity");
	        referenceable.set("description", "this is to test trait inheritence");
	        referenceable.set("owner", "Andrew");
	        referenceable.set("locationUri", "hdfs://localhost:8020");
	        referenceable.set("createTime", System.currentTimeMillis());
	        
	        aec.createEntity(referenceable);*/
		
		//aec.createEntity(aec.createRefObject("GOD_Type",, ));
		//aec.createEntity(aec.createRefObject("GOD_Type", "GreenEntity", "this is to test trait inheritence"));	
	}
	
	

}
