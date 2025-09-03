package com.movieticketbooking.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movieticketbooking.entity.Location;
import com.movieticketbooking.entity.Theatre;

@Repository
public interface TheatreDao extends JpaRepository<Theatre, Integer> {

	List<Theatre> findByStatus(String status);
	
	List<Theatre> findByLocationAndStatus(Location location, String status);

}
