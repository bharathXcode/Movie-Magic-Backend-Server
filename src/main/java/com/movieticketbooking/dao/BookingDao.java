package com.movieticketbooking.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movieticketbooking.entity.Booking;
import com.movieticketbooking.entity.Shows;
import com.movieticketbooking.entity.Theatre;
import com.movieticketbooking.entity.User;

@Repository
public interface BookingDao extends JpaRepository<Booking, Integer> {

	List<Booking> findByCustomerAndStatus(User customer, String status);

	@Query("SELECT b FROM Booking b WHERE b.show.theatre = :theatre and status =:status")
	List<Booking> findByTheatreAndStatus(@Param("theatre") Theatre theatre, @Param("status") String status);

	List<Booking> findByShow(Shows show);

	List<Booking> findByShowAndStatus(Shows show, String status);

	List<Booking> findByStatus(String status);

}
