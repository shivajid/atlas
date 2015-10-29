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

public class TypeInheritence {
	{
		System.setProperty("atlas.conf", "conf");
	}
	
	@SuppressWarnings("unused")
	private  AtlasClient ac = null;
	
	
	public TypeInheritence(String baseurl) {
		
		ac = new AtlasClient(baseurl);
		try {
			ac.createType(this.createTraitTypes());
		} catch (AtlasServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if(args.length < 0)
			throw new Exception("Please pass the atlas base url");
		String baseurl = args[0];
		
		System.out.println(" Baseurl" + baseurl);
		//TypeInheritence tIh = new TypeInheritence(baseurl);
		//tIh.createTraitTypes();
		
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
	
	/*
	 * 
	 */
	public String createTraitTypes(){
		
		return TypesSerialization.toJson(TypeUtils.getTypesDef(
				ImmutableList.<EnumTypeDefinition> of(),
				ImmutableList.<StructTypeDefinition> of(),
		ImmutableList.of(TypesUtil.createTraitTypeDef("PII", ImmutableList.of("Blue","White") )),
		ImmutableList.<HierarchicalTypeDefinition<ClassType>>of()));
		
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
	
	

}
