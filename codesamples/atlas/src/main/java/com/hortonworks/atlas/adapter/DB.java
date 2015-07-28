package com.hortonworks.atlas.adapter;

/**
 * 
 * This class represents a DB
 * @author sdutta
 *
 */

public class DB {

	
	String name =  null;
	
	public DB(){
		
	}
	
	public DB(String dbname){
		
		this.name = dbname;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		
	}
}
