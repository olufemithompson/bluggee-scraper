package com.bluggee;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bluggee.models.Content;
import com.bluggee.models.RegId;

public interface RegIdRepository extends CrudRepository<RegId, Long> {
	
	@Query(value = "select o from RegId o where o.reg =:reg")
	List<RegId> findByUniqueId(Pageable pageable, @Param("reg") String RegId);
	
	
	@Query(value = "select o from RegId o where o.deviceId =:deviceId")
	List<RegId> findByDevice(Pageable pageable, @Param("deviceId") String deviceId);
	
	
	
	@Query(value = "select o from RegId o")
	List<RegId> loadAll(Pageable pageable);
	
	
}
