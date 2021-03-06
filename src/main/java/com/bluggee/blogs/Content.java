package com.bluggee.blogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bluggee.DBObject;
import com.bluggee.DbConnection;


public class Content extends DBObject{
	long id;
	String title;
	String description;
	String image;
	String url;
	String originalUrl;
	String uniqueId;
	long sourceId;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	
	public void reset(){
		title="";
		description="";
		image="";
		url="";
		uniqueId="";
		originalUrl="";
	}
	
	

	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getImage() {
		return image;
	}



	public void setImage(String image) {
		this.image = image;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public String getOriginalUrl() {
		return originalUrl;
	}



	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}



	public String getUniqueId() {
		return uniqueId;
	}



	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}



	public long getSourceId() {
		return sourceId;
	}



	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}


	
	

	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public String getInsertSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("insert ignore into content(" +
				"title," +
				"description," +
				"image," +
				"url," +
				"original_url," +
				"unique_id," +
				"source_id," +
				"sitemap_completed," +
				"saved_date" +
				") values(")
				  .append("?").append(",")
				  .append("?").append(",")
				  .append("?").append(",")
				  .append("?").append(",")
				  .append("?").append(",")
				  .append("?").append(",")
				  .append("?").append(",")
				  .append("?").append(",")
				  .append("?").append(")");
		return sqlBuilder.toString();
	}
	
	
	
	public static boolean isPresent(DbConnection connection, String uniqueId) throws SQLException{
	   
		Connection dbConnection = null;
		dbConnection = getDBConnection(connection);
		Statement statement = dbConnection.createStatement();

		ResultSet rs = statement.executeQuery("select * from content where unique_id = '"+uniqueId+"'");
		boolean hasNext = rs.first();
		
		if (statement != null) {
			statement.close();
		}
		if (dbConnection != null) {
			dbConnection.close();
		}
		return hasNext;
	}
	
	
	public static List<Content> getContactLinks(DbConnection connection) throws SQLException{
	    List<Content>  links = new ArrayList<Content>();
		Connection dbConnection = null;
		dbConnection = getDBConnection(connection);
		Statement statement = dbConnection.createStatement();

		ResultSet rs = statement.executeQuery("select * from content order by id DESC limit 500");
		while (rs.next()) {
			Content cont = new Content();
			cont.setTitle(rs.getString("title"));
			cont.setDescription(rs.getString("description"));
			cont.setUrl(rs.getString("url"));
			links.add(cont);
		}
		if (statement != null) {
			statement.close();
		}
		if (dbConnection != null) {
			dbConnection.close();
		}
		return links;
	}
	
	
	
	public int insert(DbConnection connection) throws SQLException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		dbConnection = getDBConnection(connection);
		preparedStatement = dbConnection.prepareStatement(getInsertSql(), Statement.RETURN_GENERATED_KEYS);

		preparedStatement.setString(1, title);
		preparedStatement.setString(2, description);
		preparedStatement.setString(3, image);
		preparedStatement.setString(4, url);
		preparedStatement.setString(5, originalUrl);
		preparedStatement.setString(6, uniqueId);
		preparedStatement.setLong(7, sourceId);
		preparedStatement.setInt(8, 0);
		preparedStatement.setString(9, sdf.format(new Date()));
		
		preparedStatement.executeUpdate();
		int last_inserted_id = -1;
		 ResultSet rs = preparedStatement.getGeneratedKeys();
         if(rs.next())
         {
            last_inserted_id = rs.getInt(1);
         }
		
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (dbConnection != null) {
			dbConnection.close();
		}
		return last_inserted_id;
	}
	
	
}
