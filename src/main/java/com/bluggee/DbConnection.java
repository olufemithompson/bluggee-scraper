package com.bluggee;

public class DbConnection {

	private String db;
	private String host;
	private String port;
	private String password;
	private String user;
	
	DbConnection(String host, String port, String user, String password, String db){
		this.host = host;
		this.port = port;
		this.password = password;
		this.user = user;
		this.db = db;
	}
	
	public String getConnectionString(){
		return "jdbc:mysql://" + host + ":" + port + "/"+ db+"?useUnicode=yes&characterEncoding=UTF-8";
	}

	public String getPassword() {
		return password;
	}
	public String getUser() {
		return user;
	}
	
	
}
