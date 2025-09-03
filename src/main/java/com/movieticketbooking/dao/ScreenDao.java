package com.movieticketbooking.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movieticketbooking.entity.Screen;
import com.movieticketbooking.entity.Theatre;

@Repository
public interface ScreenDao extends JpaRepository<Screen, Integer> {

	List<Screen> findByStatusAndTheatre(String status, Theatre theatre);

	List<Screen> findByStatus(String value);
	
}
