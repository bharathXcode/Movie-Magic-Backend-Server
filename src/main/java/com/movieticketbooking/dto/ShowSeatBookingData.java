package com.movieticketbooking.dto;

import java.math.BigDecimal;

import lombok.Data;

//this dto class will help customer to select the Show Seat for the Booking

@Data
public class ShowSeatBookingData extends CommonApiResponse {

	// here we will store the Booking Id (primary key of Booking entity)
	private int id;

	// below details are from table ShowSeat
	// to show the Seat Booking Details
	private String seatNumber; // Example: A1, B2

	private String seatType; // REGULAR, PREMIUM, GOLD

	private String seatPosition; // Left, Right, Middle

	private BigDecimal price;
	
	private String status;  // from Booking entity

}
