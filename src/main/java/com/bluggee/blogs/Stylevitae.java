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
public class Stylevitae extends Blog {


	public String page;

	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(Stylevitae.class);

	HttpClient httpClient;

	public boolean isDebug() {
		return isDebug;
	}

	
	public Stylevitae(HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
		
		httpClient = httpCli;
		isDebug = debug;
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	
	@Override
	public void run() {
		Util.logToConsole(isDebug(), logger,"downloading contents  from Stylevitae",false);
		getForLinks("http://www.stylevitae.com/category/style/campus-style/");
		getForLinks("http://www.stylevitae.com/category/style/look-of-the-week/");
		getForLinks("http://www.stylevitae.com/category/style/spotted-style/");
		getForLinks("http://www.stylevitae.com/category/style/street-style/");
		getForLinks("http://www.stylevitae.com/category/style/what-she-wore/");
		getForLinks("http://www.stylevitae.com/category/beauty/beauty-buzz/");
		
		
		getForLinks("http://www.stylevitae.com/category/beauty/in-her-makeup-bag-beauty/");
		getForLinks("http://www.stylevitae.com/category/beauty/beauty-review/");
		getForLinks("http://www.stylevitae.com/category/lifestyle/art-culture/");
		getForLinks("http://www.stylevitae.com/category/for-the-men/mens-fashion/");
		getForLinks("http://www.stylevitae.com/category/for-the-men/mens-style/");
		getForLinks("http://www.stylevitae.com/category/for-the-men/grooming/");
		
		
		
		getForLinks("http://www.stylevitae.com/category/brides-bows/bridal-beauty/");
		getForLinks("http://www.stylevitae.com/category/fashion/bridal/");
		getForLinks("http://www.stylevitae.com/category/people-events/weddings/");
		
		getForLinks("http://www.stylevitae.com/category/people-events/awards/");
		getForLinks("http://www.stylevitae.com/category/people-events/fashion-happenings/");
		getForLinks("http://www.stylevitae.com/category/people-events/parties-people-events/");
	}
	
	
	private void getForLinks(String link){
		try {
			page = Util.downloadPage(link, httpClient);
			Document doc = Jsoup.parse(page);
			Element bb = doc.getElementById("content");
			
			Elements divs = bb.select("article");
			for(Element div  : divs){
				String image = div.select("img").attr("src");
				String title = div.select("header").select("h1").select("a").text();
				String originalUrl =div.select("header").select("h1").select("a").attr("href");
				String desc = div.getElementsByClass("entry-content").select("p").text();
				
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
