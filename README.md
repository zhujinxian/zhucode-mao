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

	@Find(doc="student", cnd="#id")
	public Student getStudent(String id);
```

## Insert

```java
	@Insert(doc="user")
	public void insert(User user);
	
	@Insert(doc="user")
	public void insertList(List<User> userList);
	
	@Insert(doc="user")
	public void insertArray(User[] users);

	@Insert(doc="student")
	public void insertStudentJson(JSONObject student);
	
	@Insert(doc="student")
	public void insertStudentObj(Student student);
```

## Update

```java
	@Update(doc="user", cnd="{_id:#}", with="{$set: {name: #}}")
	public void update(int userId, String name);
	
	@Update(doc="student", cnd="#id", with ="{$set: {home: #}}")
	public void updateStudentHome(String id, String home);
```

## Delete

```java
	@Delete(doc="user", cnd = "#id")
	public void delete(int userId);
	
	@Delete(doc="user", cnd = "{_id:#}")
	public void deleteParas(int userId);
```

## Count

```java
	@Count(doc="user")
	public long count();
```

## Distinct

```java
	@Distinct(doc="user", key="name")
	public List<String> getAllUserNamesList();
	
	@Distinct(doc="user", key="name")
	public String[] getAllUserNamesArray();
```



