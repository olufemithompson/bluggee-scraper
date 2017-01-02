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




public class CreativeBloq extends Blog {


	public String page;

	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(CreativeBloq.class);
	HttpClient httpClient;

	public boolean isDebug() {
		return isDebug;
	}

	
	public CreativeBloq(DbConnection dbCon,HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
		dbConnection = dbCon;
		httpClient = httpCli;
		isDebug = debug;
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	
	@Override
	public void run() {
		Util.logToConsole(isDebug(), logger,"downloading contents  from Creative Bloq",false);
		getForLinks("http://www.creativebloq.com/advice");
		getForLinks("http://www.creativebloq.com/news");
		getForLinks("http://www.creativebloq.com/reviews");
		getForLinks("http://www.creativebloq.com/features");
		getForLinks("http://www.creativebloq.com/inspiration");
		
	}
	
	
	private void getForLinks(String link){
		try {
			page = Util.downloadPage(link, httpClient);
			Document doc = Jsoup.parse(page);
			Elements divs = doc.getElementsByClass("listingResult");
			for(Element div  : divs){
				String image = div.getElementsByClass("article-lead-image-wrap").attr("data-original");
				String title = div.getElementsByClass("article-name").text();
				String originalUrl = div.select("a").attr("href");
				String desc = div.getElementsByClass("synopsis").text();
				insertData(title,image,originalUrl,desc);
			}
			
			
		} catch (ClientProtocolException e) {
			Util.logToConsole(isDebug(), logger,"could not download content from Creative Bloq",true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
