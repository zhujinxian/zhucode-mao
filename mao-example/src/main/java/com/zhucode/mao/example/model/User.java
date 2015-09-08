package com.zhucode.mao.example.model;

public class User {
	private int _id;
	private String name;
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "{_id:" + _id + ", name: " + name + "}";
	}
	
}
