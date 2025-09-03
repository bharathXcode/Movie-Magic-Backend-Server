package com.movieticketbooking.utility;

import java.util.UUID;

public class BookingIdGenerator {

	public static String generateBookingId() {
		UUID uuid = UUID.randomUUID();
		String uuidHex = uuid.toString().replace("-", ""); // Remove hyphens
		String uuid16Digits = uuidHex.substring(0, 16); // Take the first 16 characters

		return uuid16Digits.toUpperCase();
	}

}
