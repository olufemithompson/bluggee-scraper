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
public class Onobello extends Blog {


	public String page;

	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(Onobello.class);

	HttpClient httpClient;

	public boolean isDebug() {
		return isDebug;
	}

	
	public Onobello(HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
	
		httpClient = httpCli;
		isDebug = debug;
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	
	@Override
	public void run() {
		Util.logToConsole(isDebug(), logger,"downloading contents  from Onobello",false);
		getForLinks("http://onobello.com/category/fashion/trends/");
		getForLinks("http://onobello.com/category/fashion/lookbook/");
		getForLinks("http://onobello.com/category/fashion/street-style/");
		getForLinks("http://onobello.com/category/fashion/show-reports/");
		getForLinks("http://onobello.com/category/beauty/trends-n-tips/");
		getForLinks("http://onobello.com/category/beauty/brands/");
		
		
		
		
	}
	
	
	private void getForLinks(String link){
		try {
			page = Util.downloadPage(link, httpClient);
			Document doc = Jsoup.parse(page);
			Element bb = doc.getElementById("gk-mainbody");
			
			Elements divs = bb.select("article");
			for(Element div  : divs){
				String image = div.select("figure").select("img").attr("src");
				String title = div.select("header").select("h2").select("a").text();
				String originalUrl =div.select("header").select("h2").select("a").attr("href");
				String desc = div.select("section").select("p").text();
				
//				System.out.println(title);
//				System.out.println(image);
//				System.out.println(originalUrl);
//				System.out.println(desc);
				insertData(title,image,originalUrl,desc);
			}
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Util.logToConsole(isDebug(), logger,"could not download content from naij",true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
