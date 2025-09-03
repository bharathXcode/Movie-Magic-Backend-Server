package com.movieticketbooking.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AddShowRequest {

	private String showDate; // Example: 2025-04-08

	private String startTime; // Example: 18:30

	private String endTime; // Example: 21:15

	private String language; // Useful for multi-language shows (e.g., Hindi / Tamil)

	private String showType; // 2D / 3D / IMAX (optional)

	private int movieId;

	private int screenId;

	private BigDecimal goldSeatPrice;

	private BigDecimal regularSeatPrice;

	private BigDecimal premiumSeatPrice;

	private int theatreId;

}

// showDate, startTime, and endTime should always be in proper format while sending the request:
//
//showDate: yyyy-MM-dd (e.g., 2025-04-26)
//
//startTime, endTime: HH:mm (e.g., 18:30)
