package com.bluggee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


import com.bluggee.blogs.BellaNaija;
import com.bluggee.blogs.LindaIkeji;

import facebook4j.FacebookException;




public class Application {


	public String page;

	DbConnection dbConnection;
	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(Application.class);
	HttpClient httpClient;
	String baseUrl;
	
	String fbId;
	String fbSecret;
	
	
	
	
   long sourceId = 1;
	public boolean isDebug() {
		return isDebug;
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			properties.load(input);
		} catch (IOException e) {
			properties = null;
			e.printStackTrace();
		}
		if (properties != null) {
			Application a = null;
			a = new Application(properties);
			try{
				a.run();
			}catch(Exception e){
				System.out.println(e.getMessage());
			}

		
		} else {
			System.out.println("could not load properies file");
		}
	}

	
	
	

	public Application(Properties properties) {
		this.properties = properties;
		baseUrl = properties.get("baseUrl").toString();
		
		fbId = properties.get("fbId").toString();
		fbSecret = properties.get("fbSecret").toString();
		
		sourceId = Long.parseLong(properties.get("sourceId").toString());
		dbConnection = getDbConnection();
		httpClient = new DefaultHttpClient();

	}
	


	
	
	/**
	 * Creates a {@link DbConnection} object from properties file using the
	 * specified prefix
	 * 
	 * @param prefix
	 * @return
	 */
	public DbConnection getDbConnection() {
		String host = properties.get("host").toString();
		String port = properties.get("port").toString();
		String user = properties.get("user").toString();
		String pass = properties.get("password").toString();
		String db = properties.get("db").toString();
		DbConnection connection = new DbConnection(host, port, user, pass, db);
		return connection;
	}

	

	public void run(){
		LindaIkeji lindaIkeji = new LindaIkeji(dbConnection, httpClient,baseUrl, 1, isDebug);
		lindaIkeji.run();
		
		
		BellaNaija bellaNaija = new BellaNaija(dbConnection, httpClient,baseUrl, 2, isDebug);
		bellaNaija.run();
	}
	
	
	
	

}
