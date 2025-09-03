package com.movieticketbooking.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movieticketbooking.entity.Movie;
import com.movieticketbooking.entity.Review;

@Repository
public interface ReviewDao extends JpaRepository<Review, Integer> {
	
	List<Review> findByMovieIn(List<Movie> movie);
	
}
