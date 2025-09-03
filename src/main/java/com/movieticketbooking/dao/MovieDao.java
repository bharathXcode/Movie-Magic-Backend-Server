package com.movieticketbooking.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movieticketbooking.entity.Movie;
import com.movieticketbooking.entity.Theatre;

@Repository
public interface MovieDao extends JpaRepository<Movie, Integer> {

	List<Movie> findByStatus(String status);

	List<Movie> findByTheatreAndStatus(Theatre theatre, String status);

}
