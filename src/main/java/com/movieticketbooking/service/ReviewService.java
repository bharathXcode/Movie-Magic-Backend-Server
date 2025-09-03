package com.movieticketbooking.service;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.movieticketbooking.dao.MovieDao;
import com.movieticketbooking.dao.ReviewDao;
import com.movieticketbooking.dao.UserDao;
import com.movieticketbooking.dto.AddReviewRequest;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.MovieReviewResponseDto;
import com.movieticketbooking.entity.Movie;
import com.movieticketbooking.entity.Review;
import com.movieticketbooking.entity.User;
import com.movieticketbooking.exception.ReviewSaveFailedException;

@Service
public class ReviewService {

	private final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

	@Autowired
	private MovieDao movieDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ReviewDao reviewDao;

	public ResponseEntity<CommonApiResponse> addReview(AddReviewRequest request) {

		LOG.info("request received for adding food review");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getUserId() == 0 || request.getMovieId() == 0 || request.getStar() == 0
				|| request.getReview() == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userDao.findById(request.getUserId()).orElse(null);

		if (user == null) {
			response.setResponseMessage("user not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Movie movie = this.movieDao.findById(request.getMovieId()).orElse(null);

		if (movie == null) {
			response.setResponseMessage("movie not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Review review = new Review();
		review.setMovie(movie);
		review.setReview(request.getReview());
		review.setStar(request.getStar());
		review.setUser(user);

		Review addedReview = this.reviewDao.save(review);

		if (addedReview == null) {
			throw new ReviewSaveFailedException("Failed to save the review");
		}

		response.setResponseMessage("Movie review added successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<MovieReviewResponseDto> fetchMovieReviews(int movieId) {

		LOG.info("request received for fetching the movie reviews");

		MovieReviewResponseDto response = new MovieReviewResponseDto();

		if (movieId == 0) {
			response.setResponseMessage("movie id missing");
			response.setSuccess(false);

			return new ResponseEntity<MovieReviewResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Movie movie = this.movieDao.findById(movieId).orElse(null);

		if (movie == null) {
			response.setResponseMessage("food not found");
			response.setSuccess(false);

			return new ResponseEntity<MovieReviewResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Review> reviews = this.reviewDao.findByMovieIn(Arrays.asList(movie));

		if (CollectionUtils.isEmpty(reviews)) {
			response.setResponseMessage("No Movie reviews yet");
			response.setSuccess(false);

			return new ResponseEntity<MovieReviewResponseDto>(response, HttpStatus.OK);
		}
		
		double averageRating = averageFoodRating(reviews);

		response.setReviews(reviews);
		response.setAverageRating(averageRating);
		response.setResponseMessage("Movie reviews fetched");
		response.setSuccess(true);

		return new ResponseEntity<MovieReviewResponseDto>(response, HttpStatus.OK);
	}
	
	private double averageFoodRating(List<Review> reviews) {

		int totalReviews = reviews.size();

		if (totalReviews == 0) {
			return 0.0;
		}

		// Calculate the sum of all the ratings
		int sum = 0;

		for (Review review : reviews) {
			sum += review.getStar();
		}

		// Calculate the average rating
		double averageRating = (double) sum / totalReviews;
		
		// Format the average rating to one decimal place
	    DecimalFormat df = new DecimalFormat("#.#");
	    averageRating = Double.parseDouble(df.format(averageRating));

		return averageRating;
	}

}
