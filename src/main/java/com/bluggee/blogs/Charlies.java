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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bluggee.Application;
import com.bluggee.Blog;
import com.bluggee.DbConnection;
import com.bluggee.Util;
import com.bluggee.models.Content;



@Service
@Scope(value = "prototype")
public class Charlies extends Blog{


	public String page;

	
	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(Charlies.class);
	
	HttpClient httpClient;

	public boolean isDebug() {
		return isDebug;
	}

	
	public Charlies(HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
	
		httpClient = httpCli;
		isDebug = debug;
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	

	@Override
	public void run() {
		Util.logToConsole(isDebug(), logger,"downloading contents  from charlies",false);
		
		try {
			page = Util.downloadPage("http://www.challies.com/", httpClient);
			Document doc = Jsoup.parse(page);
			Elements divs = doc.getElementsByClass("post-wrapper");
			for(Element div  : divs){
				Element contentWrapper = div.getElementsByClass("entry-content-wrapper").get(0);
				String img = "http:/bluggee.com/img/icon.png";
				
				Elements as = div.getElementsByTag("a");
			
				for(Element a : as){
					
					String style = a.attr("style");
					if(style != null && !style.trim().isEmpty() && style.contains("background")){
						style = style.substring(style.indexOf("'") + 1);
						style = style.substring(0,style.indexOf("'"));
						img = style;
						if(img.startsWith("/")){
							img = "http://www.challies.com"+img;
						}
					}
				}
				String title = contentWrapper.getElementsByTag("header").get(0).getElementsByTag("h1").select("a").text();
				String originalUrl = contentWrapper.getElementsByTag("header").get(0).getElementsByTag("h1").select("a").attr("href");
				String desc = contentWrapper.getElementsByClass("entry-content").get(0).select("p").get(0).text();
				insertData(title,img,originalUrl,desc);
			}
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Util.logToConsole(isDebug(), logger,"could not download content from linda ikeji",true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
