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

import java.util.ArrayList;

public class Table {
	
	
	public ArrayList<Column> clist = null;
	
	/**
	 * @return the table_name
	 */
	public String getTable_name() {
		return table_name;
	}
	/**
	 * @param table_name the table_name to set
	 */
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	/**
	 * @return the table_type
	 */
	public String getTable_type() {
		return table_type;
	}
	/**
	 * @param table_type the table_type to set
	 */
	public void setTable_type(String table_type) {
		this.table_type = table_type;
	}
	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}
	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	/**
	 * Add Column to your ArrayList of Column
	 * @param e
	 */
	public void addColumn(Column e){
		
		if(clist == null){
			this.clist = new ArrayList<Column>();
		}
		
		this.clist.add(e);
		
	}
	
	
	/*
	 * Returns the list of columns
	 */
	public ArrayList<Column> getColumnArrayList(){
		
		return clist;
		
	}
	
	
	public Table(){
		this.clist = new ArrayList<Column>();
	}
	
	
	
	
	
	private DB db  = null	;
	
	/**
	 * @param clist the clist to set
	 */
	public void setClist(ArrayList<Column> clist) {
		this.clist = clist;
	}
	/**
	 * @return the db
	 */
	public DB getDb() {
		return this.db;
	}
	/**
	 * @param db the db to set
	 */
	public void setDb(DB db) {
		this.db = db;
	}



	private String table_name = "";
	private String table_type = "";
	private String remarks = "";

}
