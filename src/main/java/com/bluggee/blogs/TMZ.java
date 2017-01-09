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
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.bluggee.Application;
import com.bluggee.Blog;
import com.bluggee.Content;
import com.bluggee.DbConnection;
import com.bluggee.Util;




public class TMZ extends Blog{


	public String page;

	
	Properties properties;
	Boolean isDebug = true;
	private Log logger = LogFactory.getLog(TMZ.class);
	
	HttpClient httpClient;

	public boolean isDebug() {
		return isDebug;
	}

	
	public TMZ(DbConnection dbCon,HttpClient httpCli, String baseUrl,long sourceId, boolean debug) {
		dbConnection = dbCon;
		httpClient = httpCli;
		isDebug = debug;
		this.sourceId=sourceId;
		this.baseUrl = baseUrl;
	}
	

	@Override
	public void run() {
		Util.logToConsole(isDebug(), logger,"downloading contents  from tmz",false);
		download("http://www.tmz.com/");
		download("http://www.tmz.com/?adid=TMZ_Web_Nav_News");
		download("http://www.tmz.com/category/tmzsports?adid=TMZ_Web_Nav_TMZSports");
		
		
	}
	
	
	
	private void download(String link){
		try {
			page = Util.downloadPage("http://www.tmz.com/", httpClient);
			Document doc = Jsoup.parse(page);
			
			
			Elements divs = doc.getElementsByClass("news");
			for(Element div  : divs){
			
				Elements dvC = div.getElementsByClass("article-content");
				String image = null;
				if(dvC.size() > 0){
					try{
						String script = dvC.select("script").get(0).html();
						script = script.substring(0,script.indexOf(");"));
						int ind = script.indexOf("shortcodes.tmzVideoEmbed({");
						script = script.substring(ind + "shortcodes.tmzVideoEmbed(".length());
						
						JSONObject obj = new JSONObject(script);
						JSONArray  pics = obj.getJSONArray("primary_image");
						image = pics.getJSONObject(0).getString("url");
					
					}catch(Exception e){
						
					}
					if(image == null){
						image = dvC.select("img").attr("src");
					}
					String title = div.getElementsByClass("headline").get(0).text();
					String originalUrl =div.getElementsByClass("headline").get(0).attr("href");
					
					
					String desc = "";
					Elements ps = dvC.select("p");
					for(Element p : ps){
						String text = p.text().trim().replace("\n", " ");
						desc +=text;
					}
					
					insertData(title,image,originalUrl,desc);
				}
				
			}
			
			
		} catch (ClientProtocolException e) {
			Util.logToConsole(isDebug(), logger,"could not download content from tmz",true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
