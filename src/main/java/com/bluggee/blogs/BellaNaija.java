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
public class BellaNaija extends Blog{


	public String page;

	
	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(BellaNaija.class);
	
	HttpClient httpClient;

	public boolean isDebug() {
		return isDebug;
	}

	
	public BellaNaija(HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
		
		httpClient = httpCli;
		isDebug = debug;
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	

	@Override
	public void run() {
		Util.logToConsole(isDebug(), logger,"downloading contents  from bella naija",false);
		
		try {
			page = Util.downloadPage("https://www.bellanaija.com/", httpClient);
			Document doc = Jsoup.parse(page);
			Elements divs = doc.getElementsByClass("home-feature-story");
			for(Element div  : divs){
				String image = div.select("img").attr("src");
				String title = div.getElementsByTag("h3").get(0).select("a").text();
				String originalUrl = div.getElementsByTag("h3").get(0).select("a").attr("href");
				String desc = div.getElementsByTag("footer").text();
				desc = desc.substring(0,desc.indexOf("Continue love this"));
				insertData(title,image,originalUrl,desc);
			}
			
			
		} catch (ClientProtocolException e) {
			Util.logToConsole(isDebug(), logger,"could not download content from linda ikeji",true);
		} catch (IOException e) {
			
		} catch (Exception e) {
			
		}
		
	}
	
	
}
