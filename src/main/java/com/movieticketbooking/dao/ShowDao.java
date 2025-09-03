package com.movieticketbooking.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movieticketbooking.entity.Movie;
import com.movieticketbooking.entity.Screen;
import com.movieticketbooking.entity.Shows;
import com.movieticketbooking.entity.Theatre;

@Repository
public interface ShowDao extends JpaRepository<Shows, Integer> {

	List<Shows> findByStatusAndTheatre(String status, Theatre theatre);

	List<Shows> findByStatusAndMovie(String status, Movie movie);

	List<Shows> findByStatusAndScreen(String status, Screen screen);

	List<Shows> findByTheatreOrderByIdDesc(Theatre theatre);

	List<Shows> findByMovie(Movie movie);

	@Query("SELECT s FROM Shows s WHERE s.theatre = :theatre AND "
			+ "(s.showDate > :currentDate OR (s.showDate = :currentDate AND s.startTime > :currentTime)) "
			+ "AND s.status = 'ACTIVE'")
	List<Shows> findUpcomingShowsByTheatreAndStatus(@Param("theatre") Theatre theatre,
			@Param("currentDate") LocalDate currentDate, @Param("currentTime") LocalTime currentTime);

	@Query("SELECT s FROM Shows s WHERE s.theatre = :theatre AND "
			+ "(s.showDate > :currentDate OR (s.showDate = :currentDate AND s.startTime > :currentTime)) "
			+ "AND s.status = 'ACTIVE' AND LOWER(s.movie.title) LIKE LOWER(CONCAT('%', :showName, '%'))")
	List<Shows> findUpcomingShowsByTheatreAndStatusAndShowNameLike(@Param("theatre") Theatre theatre,
			@Param("currentDate") LocalDate currentDate, @Param("currentTime") LocalTime currentTime,
			@Param("showName") String showName);
	
	List<Shows> findAllByOrderByIdDesc();

}
