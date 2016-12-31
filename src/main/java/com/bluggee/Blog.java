package com.bluggee;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Blog {

	public ArrayList<Content> contents = new ArrayList<Content>();
	public DbConnection dbConnection;
	public  String baseUrl;
	public long sourceId = 1;
	public void run(){
		
	}

	public ArrayList<Content> getContents() {
		return contents;
	}

	public void insertData(String title, String image, String link, String desc) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException{
		String shaString = Util.generateSha1String(title);
		String url = baseUrl+"post/"+shaString+"/"+Util.formatTitle(title);
		if(!Content.isPresent(dbConnection, shaString)){
			Content content = new Content();
			content.setDescription(desc);
			content.setImage(image);
			content.setOriginalUrl(link);
			content.setSourceId(sourceId);
			content.setTitle(title);
			content.setUniqueId(shaString);
			content.setUrl(url);
			contents.add(content);
		}
		
	}
	
}
