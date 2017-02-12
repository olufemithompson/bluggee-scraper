package com.bluggee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;





























import com.bluggee.blogs.BellaNaija;
import com.bluggee.blogs.Charlies;
import com.bluggee.blogs.CreativeBloq;
import com.bluggee.blogs.Jaguda;
import com.bluggee.blogs.LindaIkeji;
import com.bluggee.blogs.Mp3naija;
import com.bluggee.blogs.Naij;
import com.bluggee.blogs.Notjustok;
import com.bluggee.blogs.Onobello;
import com.bluggee.blogs.Stylevitae;
import com.bluggee.blogs.TMZ;
import com.bluggee.blogs.TechCabal;
import com.bluggee.blogs.TechPoint;
import com.bluggee.rss.Feed;
import com.bluggee.rss.FeedMessage;
import com.bluggee.rss.RSSFeedWriter;





public class Application {


	public String page;

	static DbConnection dbConnection;
	static Boolean isDebug = true;
	static HttpClient httpClient;
	static String baseUrl;
	static String rssDirectory;
	
	
	public static Feed rssFeed;
	
	
    long sourceId = 1;
   
    
    static ArrayList<Blog> blogs = new ArrayList<Blog>();
    static ArrayList<Content> contents = new ArrayList<Content>();

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
		
		
		
		
		String copyright = "Copyright hold by bluggee";
        String title = "Streams of blogs in one place";
        String description = "Streams of blogs in one place";
        String language = "en";
        String link = "http://www.bluggee.com";
        Calendar cal = new GregorianCalendar();
        Date creationDate = cal.getTime();
        SimpleDateFormat date_format = new SimpleDateFormat(
                        "EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
        String pubdate = date_format.format(creationDate);
        rssFeed = new Feed(title, link, description, language,
                        copyright, pubdate);
        
        if (properties != null) {
			init(properties);
			try{
				run();
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			

		
		} else {
			System.out.println("could not load properies file");
		}
	}

	
	
	public static void addFeedMessage(Content content){
		  FeedMessage feed = new FeedMessage();
          feed.setTitle(content.getTitle());
          feed.setDescription(content.getDescription());
          feed.setAuthor("rss@bluggee.com bluggee");
          feed.setGuid(content.getUrl());
          feed.setLink(content.getUrl());
          rssFeed.getMessages().add(feed);
	}
	
	
	

	public static void init(Properties properties) {
		
		baseUrl = properties.get("baseUrl").toString();
		rssDirectory = properties.get("rssDirectory").toString();
		dbConnection = getDbConnection(properties);
		httpClient = new DefaultHttpClient();
		
		blogs.add(new LindaIkeji(dbConnection, httpClient,baseUrl, 1, isDebug));
		blogs.add(new BellaNaija(dbConnection, httpClient,baseUrl, 2, isDebug));
		blogs.add(new Naij(dbConnection, httpClient,baseUrl,3, isDebug));
		blogs.add(new TechCabal(dbConnection, httpClient,baseUrl,4, isDebug));
		blogs.add(new TechPoint(dbConnection, httpClient,baseUrl,5, isDebug));
		blogs.add(new TMZ(dbConnection, httpClient,baseUrl,7, isDebug));
		blogs.add(new Charlies(dbConnection, httpClient,baseUrl,8, isDebug));
		blogs.add(new Onobello(dbConnection, httpClient,baseUrl,9, isDebug));
		blogs.add(new Stylevitae(dbConnection, httpClient,baseUrl,10, isDebug));
		blogs.add(new Jaguda(dbConnection, httpClient,baseUrl,11, isDebug));
		blogs.add(new Mp3naija(dbConnection, httpClient,baseUrl,12, isDebug));
		blogs.add(new Notjustok(dbConnection, httpClient,baseUrl,13, isDebug));
	}
	


	
	
	/**
	 * Creates a {@link DbConnection} object from properties file using the
	 * specified prefix
	 * 
	 * @param prefix
	 * @return
	 */
	public static DbConnection getDbConnection(Properties properties) {
		String host = properties.get("host").toString();
		String port = properties.get("port").toString();
		String user = properties.get("user").toString();
		String pass = properties.get("password").toString();
		String db =   properties.get("db").toString();
		DbConnection connection = new DbConnection(host, port, user, pass, db);
		return connection;
	}

	

	public static void run(){
		
		for(Blog b : blogs){
			b.run();
			contents.addAll(b.getContents());
		}
		
		Collections.shuffle(contents);
		for(Content content : contents){
			try {
				int id = content.insert(dbConnection);
				content.setId(id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		try {
			getLastItems();
			writeRss();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		doNotification();
		
	}
	
	
	public static void doNotification(){
		ArrayList<String> regs = getRegIds();
		if(regs.size() > 0 && contents.size() > 0){
			JSONArray array = new JSONArray();
			for(String reg : regs){
				array.put(reg);
			}
			
			
			JSONObject json = new JSONObject();
			JSONObject data = new JSONObject();
			
			json.put("registration_ids", array);
			StringBuilder titleBuilder = new StringBuilder();
			StringBuilder urlBuilder = new StringBuilder();
			StringBuilder idBuilder = new StringBuilder();
			StringBuilder imageBuilder = new StringBuilder();
			StringBuilder srcBuilder = new StringBuilder();
			StringBuilder srcIdBuilder = new StringBuilder();
			
			
			for(int i = 0; i < contents.size(); i++){
				Content c= contents.get(i);
				
				titleBuilder.append(c.getTitle());
				if(i < contents.size()-1){
					titleBuilder.append("<>");
				}
				
				urlBuilder.append(c.getUrl());
				if(i < contents.size()-1){
					urlBuilder.append("<>");
				}
				
				idBuilder.append(Long.toString(c.getId()));
				if(i < contents.size()-1){
					idBuilder.append("<>");
				}
				
				imageBuilder.append(c.getImage());
				if(i < contents.size()-1){
					imageBuilder.append("<>");
				}
				
				srcIdBuilder.append(Long.toString(c.getSourceId()));
				if(i < contents.size()-1){
					srcIdBuilder.append("<>");
				}
				
				String src = getSourceName(c.getSourceId());
				
				srcBuilder.append(src);
				if(i < contents.size()-1){
					srcBuilder.append("<>");
				}
				
			}
			
			
			data.put("titles", titleBuilder.toString());
			data.put("ids", idBuilder.toString());
			data.put("images", imageBuilder.toString());
			data.put("urls", urlBuilder.toString());
			data.put("sources", srcBuilder.toString());
			data.put("sourceIds", srcIdBuilder.toString());
			
			data.put("title", "New stories from bluggee");
			data.put("message", "New stories from bluggee");
			data.put("total", Integer.toString(contents.size()));
			json.put("data", data);
			

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			try {
			    HttpPost request = new HttpPost("https://android.googleapis.com/gcm/send");
			    StringEntity params = new StringEntity(json.toString());
			    request.addHeader("content-type", "application/json");
			    request.addHeader("Authorization", "key= AAAACodgkGY:APA91bFdzSHdEQ7-BSJK24DE80hzjoAwoztTl_awYPCl5BvGV5-xu8ZRjeMf_P9v9bPArEEot47SpUko3k_2EDIm-sMSsRgkMDg1dM6BrXrZZcC9abVYoNvHE-_XdBvUsynsRD2VIzQp");
			    request.setEntity(params);
			    CloseableHttpResponse resp =  httpClient.execute(request);
			    String responseString = EntityUtils.toString(resp.getEntity(),"UTF-8");
			} catch (Exception ex) {
				
			} finally {
			    try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private static ArrayList<String> getRegIds(){
		ArrayList<String> regs = new ArrayList<String>();
		Connection connection = null;
		try {
			connection = DBObject.getDBConnection(dbConnection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select reg from reg_id");
			while (rs.next()) {
				regs.add( rs.getString("reg"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return regs;
	}
	
	
	
	
	private static String getSourceName(long id){
		String name = "";
		Connection connection = null;
		try {
			connection = DBObject.getDBConnection(dbConnection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select name from blog_source where id = "+id);
			while (rs.next()) {
				name = rs.getString("name");
				
				break;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return name;
	}
	
	
	
	public static void getLastItems() throws SQLException{
		List<Content> contents = Content.getContactLinks(dbConnection);
		for(Content content : contents){
			addFeedMessage(content);
		}
	}
	
	
	public static void writeRss(){
		
		File file = new File(rssDirectory, "rss.rss");
		RSSFeedWriter writer = new RSSFeedWriter(rssFeed, file.getAbsolutePath());
        try {
                writer.write();
        } catch (Exception e) {
                e.printStackTrace();
        }
	}
	

}
