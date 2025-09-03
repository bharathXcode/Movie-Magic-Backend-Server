package com.movieticketbooking.dto;

import java.util.ArrayList;
import java.util.List;

import com.movieticketbooking.entity.Booking;

import lombok.Data;

@Data
public class BookingResponse extends CommonApiResponse {

	private List<Booking> bookings = new ArrayList<>();
	
}
