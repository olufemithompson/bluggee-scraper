package com.bluggee.blogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.bluggee.Application;
import com.bluggee.Blog;
import com.bluggee.Content;
import com.bluggee.DbConnection;
import com.bluggee.Util;




public class TechCabal extends Blog {


	public String page;

	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(TechCabal.class);
	HttpClient httpClient;

	public boolean isDebug() {
		return isDebug;
	}

	
	public TechCabal(DbConnection dbCon,HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
		dbConnection = dbCon;
		httpClient = httpCli;
		isDebug = debug;
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	
	@Override
	public void run() {
		Util.logToConsole(isDebug(), logger,"downloading contents  from Tech Cabal",false);
		getForLinks("http://techcabal.com/posts/");
		
		
		
		
		
	}
	
	
	private void getForLinks(String link){
		try {
			page = Util.downloadPage(link, httpClient);
			Document doc = Jsoup.parse(page);
			Elements divs = doc.getElementsByClass("post");
			for(Element div  : divs){
				String image = div.getElementsByClass("wp-post-image").get(0).attr("src");
				String title = div.getElementsByClass("entry-title").text();
				String originalUrl = div.getElementsByClass("entry-title").select("a").attr("href");
				String desc = div.getElementsByClass("entry-summary").select("p").get(0).text();
				insertData(title,image,originalUrl,desc);
			}
			
			
		} catch (ClientProtocolException e) {
			Util.logToConsole(isDebug(), logger,"could not download content from Tech Cabal",true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
