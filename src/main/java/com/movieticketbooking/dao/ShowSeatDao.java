package com.movieticketbooking.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movieticketbooking.entity.Shows;
import com.movieticketbooking.entity.ShowSeat;

@Repository
public interface ShowSeatDao extends JpaRepository<ShowSeat, Integer> {

	List<ShowSeat> findByShow(Shows show);

}
