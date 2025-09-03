package com.movieticketbooking.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movieticketbooking.entity.Screen;
import com.movieticketbooking.entity.ScreenSeat;

@Repository
public interface ScreenSeatDao extends JpaRepository<ScreenSeat, Integer> {

	List<ScreenSeat> findByScreen(Screen screen);

}
