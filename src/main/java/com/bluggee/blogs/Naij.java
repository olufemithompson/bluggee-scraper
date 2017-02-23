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
public class Naij extends Blog {


	public String page;

	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(Naij.class);

	HttpClient httpClient;

	public boolean isDebug() {
		return isDebug;
	}

	
	public Naij(HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
	
		httpClient = httpCli;
		isDebug = debug;
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	
	@Override
	public void run() {
		Util.logToConsole(isDebug(), logger,"downloading contents  from Naij",false);
		getForLinks("https://www.naij.com/latest/");
		getForLinks("https://politics.naij.com/");
		getForLinks("https://sports.naij.com/");
		getForLinks("https://gossip.naij.com/");
		getForLinks("https://www.naij.com/weddings/");
		
		
		
		
		
	}
	
	
	private void getForLinks(String link){
		try {
			page = Util.downloadPage(link, httpClient);
			Document doc = Jsoup.parse(page);
			Elements divs = doc.getElementsByClass("news-horizontal");
			for(Element div  : divs){
				String image = div.getElementsByClass("news__image").get(0).attr("src");
				String title = div.getElementsByClass("news-horizontal__caption").text();
				String originalUrl = div.getElementsByClass("news__link").attr("href");
				String desc = div.getElementsByClass("news-horizontal__description").text();
				insertData(title,image,originalUrl,desc);
			}
			
			
		} catch (ClientProtocolException e) {
			Util.logToConsole(isDebug(), logger,"could not download content from naij",true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
