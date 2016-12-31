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
import com.bluggee.Content;
import com.bluggee.DbConnection;
import com.bluggee.Util;




public class Naij {


	public String page;

	DbConnection dbConnection;
	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(Naij.class);
	String baseUrl;
	Content content;
	HttpClient httpClient;
	private long sourceId = 3;

	public boolean isDebug() {
		return isDebug;
	}

	
	public Naij(DbConnection dbCon,HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
		dbConnection = dbCon;
		httpClient = httpCli;
		isDebug = debug;
		content = new Content();
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	

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
	
	private void insertData(String title, String image, String link, String desc) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException{
		String shaString = Util.generateSha1String(title);
		String url = baseUrl+"post/"+shaString+"/"+Util.formatTitle(title);
		if(!Content.isPresent(dbConnection, shaString)){
			content.reset();
			content.setDescription(desc);
			content.setImage(image);
			content.setOriginalUrl(link);
			content.setSourceId(sourceId);
			content.setTitle(title);
			content.setUniqueId(shaString);
			content.setUrl(url);
			content.insert(dbConnection);
		}
		
	}
}