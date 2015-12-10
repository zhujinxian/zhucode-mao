# zhucode-mao
MAO is a java interface for mapping data from mongodb to objects.
## Interface

```java
	@MAO(db="hello")
	public interface HelloMAO {

	}
```

## Querying

```java
	@Find(coll="user", cnd="{_id:1}")
	User getUser();
	
	@Find(coll="user", cnd="")
	MongoCursor<User> getUserIt();
	
	@Find(coll="user", cnd="{_id:#}")
	User getUser(int id);
	
	@Find(coll="user")
	List<User> getUserList();
	
	@Find(coll="user")
	User[] getUserArray();
	
	@Find(coll="user", cnd="{_id:1}")
	BasicDBObject getUserDBObject();
	
	@Find(coll="user", cnd="{_id:1}")
	JSONObject getUserJson();

	@Find(coll="student", cnd="#id")
	public Student getStudent(String id);
```

## Insert

```java
	@Insert(coll="user")
	public void insert(User user);
	
	@Insert(coll="user")
	public void insertList(List<User> userList);
	
	@Insert(coll="user")
	public void insertArray(User[] users);

	@Insert(coll="student")
	public void insertStudentJson(JSONObject student);
	
	@Insert(coll="student")
	public void insertStudentObj(Student student);
```

## Update

```java
	@Update(coll="user", cnd="{_id:#}", with="{$set: {name: #}}")
	public void update(int userId, String name);
	
	@Update(coll="student", cnd="#id", with ="{$set: {home: #}}")
	public void updateStudentHome(String id, String home);
```

## Delete

```java
	@Delete(coll="user", cnd = "#id")
	public void delete(int userId);
	
	@Delete(coll="user", cnd = "{_id:#}")
	public void deleteParas(int userId);
```

## Count

```java
	@Count(coll="user")
	public long count();
```

## Distinct

```java
	@Distinct(coll="user", key="name")
	public List<String> getAllUserNamesList();
	
	@Distinct(coll="user", key="name")
	public String[] getAllUserNamesArray();
```



