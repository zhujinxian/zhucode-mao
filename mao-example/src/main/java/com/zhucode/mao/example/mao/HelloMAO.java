package com.zhucode.mao.example.mao;

import java.util.List;

import org.jongo.MongoCursor;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.zhucode.mao.example.model.Student;
import com.zhucode.mao.example.model.User;
import com.zhucode.mao.mongo.annotation.Count;
import com.zhucode.mao.mongo.annotation.Delete;
import com.zhucode.mao.mongo.annotation.Distinct;
import com.zhucode.mao.mongo.annotation.Find;
import com.zhucode.mao.mongo.annotation.Insert;
import com.zhucode.mao.mongo.annotation.MAO;
import com.zhucode.mao.mongo.annotation.Update;
import com.zhucode.mao.mongo.generic.GenericMAO;

@MAO(db="hello")
public interface HelloMAO extends GenericMAO {
	
	@Find(doc="user", cnd="{_id:1}")
	User getUser();
	
	@Find(doc="user", cnd="")
	MongoCursor<User> getUserIt();
	
	@Find(doc="user", cnd="{_id:#}")
	User getUser(int id);
	
	@Find(doc="user")
	List<User> getUserList();
	
	@Find(doc="user")
	User[] getUserArray();
	
	@Find(doc="user", cnd="{_id:1}")
	BasicDBObject getUserDBObject();
	
	@Find(doc="user", cnd="{_id:1}")
	JSONObject getUserJson();
	
	//================================
	
	@Insert(doc="user")
	public void insert(User user);
	
	@Insert(doc="user")
	public void insertList(List<User> userList);
	
	@Insert(doc="user")
	public void insertArray(User[] users);
	
	//====================
	
	@Delete(doc="user", cnd = "#id")
	public void delete(int userId);
	
	
	@Delete(doc="user", cnd = "{_id:#}")
	public void deleteParas(int userId);
	
	//====================	
	
	@Update(doc="user", cnd="{_id:#}", with="{$set: {name: #}}")
	public void update(int userId, String name);
	
	//========================
	
	@Count(doc="user")
	public long count();
	
	//=========================
	
	@Distinct(doc="user", key="name")
	public List<String> getAllUserNamesList();
	
	@Distinct(doc="user", key="name")
	public String[] getAllUserNamesArray();
	
	//==========================
	
	@Insert(doc="student")
	public void insertStudentJson(JSONObject student);
	
	@Insert(doc="student")
	public void insertStudentObj(Student student);
	
	@Find(doc="student", cnd="#id")
	public Student getStudent(String id);
	
	@Find(doc="student", cnd="{_id:#}")
	public Student getStudentPars(String id);
	
	@Update(doc="student", cnd="#id", with ="{$set: {home: #}}")
	public void updateStudentHome(String id, String home);
	
	
}
