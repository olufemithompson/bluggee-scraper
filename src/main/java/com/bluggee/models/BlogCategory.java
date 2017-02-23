package com.bluggee.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Entity
public class BlogCategory {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
    
    @Field
	private String name;
    
    
	public BlogCategory() {
		super();
	}



	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	

	
	
	
	
	
	
}
