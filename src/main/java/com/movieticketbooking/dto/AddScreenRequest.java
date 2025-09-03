package com.movieticketbooking.dto;

import lombok.Data;

@Data
public class AddScreenRequest {

	private String name; // Example: Screen A, B, C

	private int totalSeats;

	// below seat details are for dynamically showing the Seat Structure in UI to
	// the customer
	// note: pricing of seat we will keep based on the Show running in Shows Entity
	private int totalRegularSeats;
	
	private int totalRegularSeatRow;

	private int totalPremiumSeats;

	private int totalPremiumSeatRow;
	
	private int totalGoldSeats;
	
	private int totalGoldSeatRow;

	private int totalLeftSideSeats;

	private int totalRightSideSeats;

	private int totalMiddleSeats;

	private int theatreId;
	
}
