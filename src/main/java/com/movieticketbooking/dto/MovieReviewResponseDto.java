package com.movieticketbooking.dto;

import java.util.ArrayList;
import java.util.List;

import com.movieticketbooking.entity.Review;

import lombok.Data;

@Data
public class MovieReviewResponseDto extends CommonApiResponse {
	
	private List<Review> reviews = new ArrayList<>();
	
	private double averageRating = 0.0;

}
