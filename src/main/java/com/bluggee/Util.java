package com.bluggee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;




public class Util {

	static HashMap<String,String> cookies = new HashMap<String,String>();
	public static String firefoxPath;
	public static int numTries = 5;
	static Facebook facebook;
	
	/**
	 * causes the thread to wait for a specific amount of time
	 * 
	 * @param time in  millisecs
	 */
	public static void waitALittle(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	

	public static void logToConsole(Boolean isDebug,Log logger, String message, boolean isError){
		if(isDebug){
			if(isError){
				logger.error(message);
			}else{
				logger.info(message);
			}
			
		}
	}




	/**
	 * Go to a particular page and download it
	 * 
	 * 
	 */
	public static String downloadPage(String location, HttpClient client)
			throws ClientProtocolException, IOException {
		HttpGet cinRequest = new HttpGet(location);
		Util.setDefaultRequestHeaders(cinRequest);
		cinRequest
				.addHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

		HttpResponse response = client.execute(cinRequest);
		String responseString = EntityUtils.toString(response.getEntity(),
				"UTF-8");
		cinRequest.releaseConnection();
		return responseString;
	}

	
	
	
	/**
	 * Sets default http request headers
	 * 
	 * @param request
	 */
	public static void setDefaultRequestHeaders(HttpRequestBase request) {
		request.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:32.0) Gecko/20100101 Firefox/32.0");
		request.addHeader("Accept-Language", "en-US,en;q=0.8");
	}

	
	
	public static String formatTitle(String title){
		String formattedTitle = ""+title;
		formattedTitle = formattedTitle.replaceAll("[^a-zA-Z0-9 ]", "");
		formattedTitle = formattedTitle.replace(" ", "-");
		formattedTitle = formattedTitle.toLowerCase();
		return formattedTitle;
	}
	
	
	
	public static String generateSha1String(String string)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(string.getBytes("iso-8859-1"), 0, string.length());
		sha1hash = md.digest();
		return convertToHex(sha1hash);
	}

	
	
	public static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					buf.append((char) ('0' + halfbyte));
				} else {
					buf.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	
	public static void initFaceBook(String fbId, String fbSecret) throws FacebookException{
		facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(fbId, fbSecret);
		facebook.setOAuthPermissions("publish_pages");
		facebook.setOAuthAccessToken(facebook.getOAuthAccessToken("EAACEdEose0cBAN4RIVysEwStr3SRxrhNayOb6bQbIMfS0ZBtDsWerXImEBVDHGZASQSVXJznZAqyxzdeBSekDCymzgu4KqWIqsidbHqfmonrCmDftIrkehZAnDmArTX46nlM7lyzFzdIOTRjkx4DD0VRJZAXs80hb2ox4DRhotAZDZD",null));
	}
	
	
	
	
	public static void postToFacebook(String link) throws MalformedURLException, FacebookException{
		//facebook.postStatusMessage("Hello World from Facebook4J.");
		
		
		FacebookClient facebookClient = new DefaultFacebookClient("555586027980853|ynWus9nLJuf0X4NNLG5ahGp1XD0");
	
		FacebookType publishMessageResponse = facebookClient.publish("/546349135390552/feed", FacebookType.class, Parameter.with("link", link));
		//facebook.postLink(new URL(link));
	}

	
}
