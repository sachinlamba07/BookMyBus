package com.masai.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.masai.model.Bus;

//JPA Repository is mainly used for managing the data in a Spring Boot Application.
@Repository
public interface busRepository extends JpaRepository<Bus, Integer> {

	public List<Bus> findByBusType(String busType);

}
