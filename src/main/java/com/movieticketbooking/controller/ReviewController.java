package com.movieticketbooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movieticketbooking.dto.AddReviewRequest;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.MovieReviewResponseDto;
import com.movieticketbooking.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/movie/review")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {
	
	@Autowired
	private ReviewService reviewService;
	
	@PostMapping("add")
	@Operation(summary = "Api to add movie review")
	public ResponseEntity<CommonApiResponse> addReview(@RequestBody AddReviewRequest request) {
		return this.reviewService.addReview(request);
	}
	
	@GetMapping("fetch")
	@Operation(summary = "Api to fetch movie reviews")
	public ResponseEntity<MovieReviewResponseDto> fetchFoodReview(@RequestParam("movieId") int movieId) {
		return this.reviewService.fetchMovieReviews(movieId);
	}

}
