package com.movieticketbooking.dto;

import lombok.Data;

@Data
public class AddBookingRequest {

	// with comma separated 12,15,16,18
	private String bookingIds; // this id is primary key of Booking entity

	private int customerId;

}
