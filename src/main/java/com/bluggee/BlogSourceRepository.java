package com.bluggee;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bluggee.models.BlogSource;



public interface BlogSourceRepository extends CrudRepository<BlogSource, Long> {

	@Query(value = "select o from BlogSource o where o.category.id=:id")
    List<BlogSource> listByCat(@Param("id")  Long id);
	
}
