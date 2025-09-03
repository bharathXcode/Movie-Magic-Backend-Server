package com.movieticketbooking.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ShowSeatBookingResponseDto extends CommonApiResponse {

	private List<ShowSeatBookingData> bookings = new ArrayList<>();

}
