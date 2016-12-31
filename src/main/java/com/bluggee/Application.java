package com.bluggee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;














import com.bluggee.blogs.BellaNaija;
import com.bluggee.blogs.LindaIkeji;
import com.bluggee.blogs.Naij;
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
//		LindaIkeji lindaIkeji = new LindaIkeji(dbConnection, httpClient,baseUrl, 1, isDebug);
//		lindaIkeji.run();
//		
//		
//		BellaNaija bellaNaija = new BellaNaija(dbConnection, httpClient,baseUrl, 2, isDebug);
//		bellaNaija.run();
		
		
//		Naij naij = new Naij(dbConnection, httpClient,baseUrl,3, isDebug);
//		naij.run();
//		
//		
//		TechCabal tc = new TechCabal(dbConnection, httpClient,baseUrl,4, isDebug);
//		tc.run();
		
		
		TechPoint tp = new TechPoint(dbConnection, httpClient,baseUrl,5, isDebug);
		tp.run();
		
		try {
			getLastItems();
			writeRss();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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
