package com.bluggee;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bluggee.models.Content;




@Service
@Scope(value = "prototype")
public class Blog {

	public ArrayList<Content> contents = new ArrayList<Content>();
	
	
	public  String baseUrl;
	
	
	@Autowired
	ContentRepository repository;
	 
	 
	@Autowired
	BlogSourceRepository srepository;
	
	
	public long sourceId = 1;
	
	
	public void run(){
		
	}

	public ArrayList<Content> getContents() {
		return contents;
	}
	
	

	public void insertData(String title, String image, String link, String desc) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException{
		String shaString = Util.generateSha1String(title);
		String url = baseUrl+"post/"+shaString+"/"+Util.formatTitle(title);
		 PageRequest pageable = new PageRequest(0,1);
		
		if(repository.findByUniqueId(pageable, shaString).size() == 0){
			Content content = new Content();
			content.setDescription(desc);
			content.setImage(image);
			content.setSavedDate(new Date());
			content.setOriginalUrl(link);
			content.setSource(srepository.findOne(sourceId));
			content.setTitle(title);
			content.setUniqueId(shaString);
			content.setUrl(url);
			contents.add(content);
		}
		
		
		
	}
	
}
