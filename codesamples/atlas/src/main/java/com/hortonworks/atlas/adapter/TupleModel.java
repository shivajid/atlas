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
