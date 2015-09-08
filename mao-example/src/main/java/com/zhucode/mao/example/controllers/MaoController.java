package com.zhucode.mao.example.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.paoding.rose.web.Invocation;
import net.paoding.rose.web.annotation.Path;
import net.paoding.rose.web.annotation.rest.Get;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.zhucode.mao.example.mao.HelloMAO;
import com.zhucode.mao.example.model.Student;
import com.zhucode.mao.example.model.User;

@Path("/mao")
public class MaoController {

	@Autowired
	HelloMAO mao;

	@Get(value = "find")
	public void findAction(Invocation inv) {

		try {
			PrintWriter pw = inv.getResponse().getWriter();

			pw.println("user-name 1= " + mao.getUser().toString());
			pw.println("user-name 2= " + mao.getUser(1).toString());
			pw.println("user-name 3= " + mao.getUserIt().next());

			pw.println("user-name 4= " + mao.getUserArray());
			pw.println("user-name 5= " + mao.getUserList());

			pw.println("user-name 6= " + mao.getUserDBObject());
			pw.println("user-name 7= " + mao.getUserJson());

		} catch (Exception e) {
			PrintWriter pw;
			try {
				pw = inv.getResponse().getWriter();
				pw.println(e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

	@Get(value = "ins")
	public String insAction(Invocation inv) {

		User user = new User();

		user.set_id(100);
		user.setName("100");
		mao.insert(user);

		user.set_id(101);
		user.setName("101");
		List<User> userList = new ArrayList<User>();
		userList.add(user);
		mao.insertList(userList);

		user.set_id(102);
		user.setName("102");
		User[] users = new User[] { user };
		mao.insertArray(users);

		return "@success";

	}

	@Get(value = "del")
	public String delAction(Invocation inv) {

		mao.delete(100);
		mao.deleteParas(101);
		mao.delete(102);

		return "@success";

	}

	@Get(value = "update")
	public String updAction(Invocation inv) {

		mao.update(1, "test update");

		return "@success";

	}

	@Get(value = "count")
	public String countAction(Invocation inv) {

		return "@success: " + mao.count();

	}

	@Get(value = "distinct")
	public String distinctAction(Invocation inv) {

		System.out.println(Arrays.asList(mao.getAllUserNamesArray()));
		System.out.println(mao.getAllUserNamesList());

		return "@success";

	}

	@Get(value = "doc")
	public String docAction(Invocation inv) {

		return "@success: " + mao.getCollection("user").getName();

	}

	@Get(value = "student")
	public String strAction(Invocation inv) {

		JSONObject xiaoming = new JSONObject();
		xiaoming.put("_id", "1");
		xiaoming.put("name", "xiaoming");

		Student xiaohong = new Student();
		xiaohong.setName("xiaohong");
		xiaohong.set_id("2");
		xiaohong.setHome("xiaohong's home");

		// mao.insertStudentJson(xiaoming);
		// mao.insertStudentObj(xiaohong);

		mao.updateStudentHome("1", "xiaoming's home2");

		System.out.println(mao.getStudent("2"));
		System.out.println(mao.getStudentPars("1"));

		return "@success: ";

	}
}
