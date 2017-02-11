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




public class Mp3naija extends Blog {


	public String page;

	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(Mp3naija.class);

	HttpClient httpClient;

	public boolean isDebug() {
		return isDebug;
	}

	
	public Mp3naija(DbConnection dbCon,HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
		dbConnection = dbCon;
		httpClient = httpCli;
		isDebug = debug;
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	
	@Override
	public void run() {
		Util.logToConsole(isDebug(), logger,"downloading contents  from Mp3 naija",false);
		
		
		getForLinks("http://www.mp3naija.com.ng/");
//		getForLinks("http://jaguda.com/music/afro-pop/");
//		getForLinks("http://jaguda.com/music/alternative/");
//		getForLinks("http://jaguda.com/music/fuji/");
//		getForLinks("http://jaguda.com/music/gospel/");
//		getForLinks("http://jaguda.com/music/highlife/");
//		
//		
//		getForLinks("http://jaguda.com/music/hip-hop/");
//		getForLinks("http://jaguda.com/music/reggaedancehall/");
//		getForLinks("http://jaguda.com/music/rnb/");
//		getForLinks("http://jaguda.com/mixtapes/");
		
		
	}
	
	
	private void getForLinks(String link){
		try {
			page = Util.downloadPage(link, httpClient);
//			System.out.println("\n\n");
//			System.out.println(link);
//			System.out.println("---------------");
			Document doc = Jsoup.parse(page);
//			Element bb = doc.getElementsByClass("def-block").get(0);
//			System.out.println(bb);
			Elements divs = doc.getElementsByClass("news");
			for(Element div  : divs){
				String image = div.getElementsByClass("span5").select("img").attr("src");
				String title = div.getElementsByClass("span7").select("h2").select("a").text();
				String originalUrl = div.getElementsByClass("span7").select("h2").select("a").attr("href");
				String desc =  div.getElementsByClass("span7").select("p").text();
				
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
