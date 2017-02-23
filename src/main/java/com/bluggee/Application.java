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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.json.JSONArray;
import org.json.JSONObject;


import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;

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
import com.bluggee.models.Content;
import com.bluggee.rss.Feed;
import com.bluggee.rss.FeedMessage;
import com.bluggee.rss.RSSFeedWriter;




@SpringBootApplication
public class Application implements CommandLineRunner{


	public String page;

	static DbConnection dbConnection;
	
	@PersistenceContext
	private EntityManager entityManager;
	
    Boolean isDebug = true;
	
     HttpClient httpClient;
	
	@Value("${baseUrl}")
    String baseUrl;
	
	@Value("${rssDirectory}")
	String rssDirectory;
	
	
	public  Feed rssFeed;
	
	
	 @Autowired
	 BeanFactory beanFactory;
	
	@Autowired
	ContentRepository repository;
	
    long sourceId = 1;
   
    
    static ArrayList<Blog> blogs = new ArrayList<Blog>();
    
    static ArrayList<Content> contents = new ArrayList<Content>();

    @Override
    public void run(String... args) throws Exception {
	
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
        
        
        init();
        try{
			doRun(args.length > 0);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
       
    }
    
    
    @Bean
    public Blog createBlog(){
    	return new Blog();
    }
    
    
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args).close();
	}

	
	
	public void addFeedMessage(Content content){
		  FeedMessage feed = new FeedMessage();
          feed.setTitle(content.getTitle());
          feed.setDescription(content.getDescription());
          feed.setAuthor("rss@bluggee.com bluggee");
          feed.setGuid(content.getUrl());
          feed.setLink(content.getUrl());
          rssFeed.getMessages().add(feed);
	}
	
	
	

	public  void init() {
		httpClient = new DefaultHttpClient();
		blogs.add(beanFactory.getBean(LindaIkeji.class,httpClient,baseUrl, 1, isDebug));
		blogs.add(beanFactory.getBean(BellaNaija.class,httpClient,baseUrl, 2, isDebug));
		blogs.add(beanFactory.getBean(Naij.class,httpClient,baseUrl,3, isDebug));
		blogs.add(beanFactory.getBean(TechCabal.class,httpClient,baseUrl,4, isDebug));
		blogs.add(beanFactory.getBean(TechPoint.class,httpClient,baseUrl,5, isDebug));
		blogs.add(beanFactory.getBean(TMZ.class,httpClient,baseUrl,7, isDebug));
		blogs.add(beanFactory.getBean(Charlies.class, httpClient,baseUrl,8, isDebug));
		blogs.add(beanFactory.getBean(Onobello.class, httpClient,baseUrl,9, isDebug));
		blogs.add(beanFactory.getBean(Stylevitae.class,httpClient,baseUrl,10, isDebug));
		blogs.add(beanFactory.getBean(Jaguda.class,httpClient,baseUrl,11, isDebug));
		blogs.add(beanFactory.getBean(Mp3naija.class, httpClient,baseUrl,12, isDebug));
		blogs.add(beanFactory.getBean(Notjustok.class,httpClient,baseUrl,13, isDebug));
	}
	


	
	public void reindex(){
		try {
			 System.out.println("reindexing data");
		      FullTextEntityManager fullTextEntityManager =
		      Search.getFullTextEntityManager(entityManager);
		      fullTextEntityManager.createIndexer().startAndWait();
		      fullTextEntityManager.flushToIndexes();
		      System.out.println("finish reindexing data");
		      
		    }
		    catch (InterruptedException e) {
		      System.out.println(
		        "An error occurred trying to build the serach index: " +
		         e.toString());
		    }
	}
	
	
	/**
	 * Creates a {@link DbConnection} object from properties file using the
	 * specified prefix
	 * 
	 * @param prefix
	 * @return
	 */
	public  DbConnection getDbConnection(Properties properties) {
		String host = properties.get("host").toString();
		String port = properties.get("port").toString();
		String user = properties.get("user").toString();
		String pass = properties.get("password").toString();
		String db =   properties.get("db").toString();
		DbConnection connection = new DbConnection(host, port, user, pass, db);
		return connection;
	}

	

	public void doRun(boolean reindex){
		
		for(Blog b : blogs){
			b.run();
			contents.addAll(b.getContents());
		}
		
		Collections.shuffle(contents);
		for(Content content : contents){
			repository.save(content);
		}
		
		if(reindex){
			reindex();
		}
		
		
		try {
			getLastItems();
			writeRss();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		doNotification();
		
	}
	
	
	public  void doNotification(){
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
			StringBuilder ourlBuilder = new StringBuilder();
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
				
				ourlBuilder.append(c.getOriginalUrl());
				if(i < contents.size()-1){
					ourlBuilder.append("<>");
				}

				
				
				idBuilder.append(Long.toString(c.getId()));
				if(i < contents.size()-1){
					idBuilder.append("<>");
				}
				
				imageBuilder.append(c.getImage());
				if(i < contents.size()-1){
					imageBuilder.append("<>");
				}
				
				srcIdBuilder.append(Long.toString(c.getSource().getId()));
				if(i < contents.size()-1){
					srcIdBuilder.append("<>");
				}
				
				String src = getSourceName(c.getSource().getId());
				
				srcBuilder.append(src);
				if(i < contents.size()-1){
					srcBuilder.append("<>");
				}
				
			}
			
			
			data.put("titles", titleBuilder.toString());
			data.put("ids", idBuilder.toString());
			data.put("images", imageBuilder.toString());
			data.put("urls", urlBuilder.toString());
			data.put("ourls", ourlBuilder.toString());
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
	
	
	private  ArrayList<String> getRegIds(){
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
	
	
	
	
	private  String getSourceName(long id){
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
	
	
	
	public  void getLastItems() throws SQLException{
	    PageRequest pageable = new PageRequest(0,500);
		List<Content> contents = repository.list(pageable);
		for(Content content : contents){
			addFeedMessage(content);
		}
	}
	
	
	public  void writeRss(){
		
		File file = new File(rssDirectory, "rss.rss");
		RSSFeedWriter writer = new RSSFeedWriter(rssFeed, file.getAbsolutePath());
        try {
                writer.write();
        } catch (Exception e) {
                e.printStackTrace();
        }
	}
	

}
