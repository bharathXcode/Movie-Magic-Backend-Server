package com.movieticketbooking.dto;

import lombok.Data;

@Data
public class AddReviewRequest {

	private int userId;

	private int movieId;

	private int star;

	private String review;

}
