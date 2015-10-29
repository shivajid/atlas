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

public class Column {

	
	private int size = 0;
	
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	
	/**
	 * @return the column_name
	 */
	public String getColumn_name() {
		return column_name;
	}
	/**
	 * @param column_name the column_name to set
	 */
	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
	/**
	 * @return the column_type
	 */
	public String getColumn_type() {
		return column_type;
	}
	/**
	 * @param column_type the column_type to set
	 */
	public void setColumn_type(String column_type) {
		this.column_type = column_type;
	}
	/**
	 * @return the column_remarks
	 */
	public String getColumn_remarks() {
		return column_remarks;
	}
	/**
	 * @param column_remarks the column_remarks to set
	 */
	public void setColumn_remarks(String column_remarks) {
		this.column_remarks = column_remarks;
	}
	private String column_name = null;
	private String column_type =  null;
	private String column_remarks =  null;
	/**
	 * @return the column_size
	 */
	public String getColumn_size() {
		return column_size;
	}
	/**
	 * @param column_size the column_size to set
	 */
	public void setColumn_size(String column_size) {
		this.column_size = column_size;
	}
	private String column_size =  null;
}
