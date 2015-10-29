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

package com.hortonworks.atlas.adapter;

public class TupleModel {

	private int level = 0;
	
	
	
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	private String currnode = null;
	public String getCurrnode() {
		return currnode;
	}
	public void setCurrnode(String currnode) {
		this.currnode = currnode;
	}
	public String getParentnode() {
		return parentnode;
	}
	public void setParentnode(String parentnode) {
		this.parentnode = parentnode;
	}
	private String parentnode = null;
	
	
	public TupleModel(String cr, String pa){
		
		this.currnode = cr;
		this.parentnode = pa;
	}
	
}
