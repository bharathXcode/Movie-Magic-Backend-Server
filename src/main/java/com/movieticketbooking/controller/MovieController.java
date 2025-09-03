package com.movieticketbooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movieticketbooking.dto.AddMovieRequest;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.MovieResponse;
import com.movieticketbooking.dto.UpdateMovieDetailRequest;
import com.movieticketbooking.service.MovieService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/movie")
@CrossOrigin(origins = "http://localhost:3000")
public class MovieController {

	@Autowired
	private MovieService movieService;

	@PostMapping("add")
	@Operation(summary = "Api to add the movie")
	public ResponseEntity<CommonApiResponse> addTheatreMovie(AddMovieRequest request) {
		return this.movieService.addTheatreMovie(request);
	}
	
	@PutMapping("update/detail")
	@Operation(summary = "Api to update theatre movie details")
	public ResponseEntity<CommonApiResponse> updateTheatreMovieDetails(@RequestBody UpdateMovieDetailRequest request) {
		System.out.println(request);
		return this.movieService .updateTheatreMovieDetails(request);
	}

	@PutMapping("update/poster")
	@Operation(summary = "Api to update theatre movie image")
	public ResponseEntity<CommonApiResponse> updateTheatreImage(AddMovieRequest request) {
		return this.movieService.updateTheatreImage(request);
	}
	
	@PutMapping("update/status")
	@Operation(summary = "Api to update the Theatre Movie status")
	public ResponseEntity<CommonApiResponse> updateTheatreStatus(@RequestParam("movieId") int movieId,
			@RequestParam("status") String status) {
		return this.movieService.updateMovieStatus(movieId, status);
	}

	// for admin
	@GetMapping("/fetch/status-wise")
	@Operation(summary = "Api to all movies by status")
	public ResponseEntity<MovieResponse> fetchTheatreByStatus(@RequestParam("status") String status) {
		return movieService.fetchAllMoviesByStatus(status);
	}

	@GetMapping("/fetch/theatre-wise")
	@Operation(summary = "Api to all movies by the theatres")
	public ResponseEntity<MovieResponse> fetchMoviesByTheatre(@RequestParam("theatreId") Integer theatreId) {
		return movieService.fetchMoviesByTheatre(theatreId);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete the movie")
	public ResponseEntity<CommonApiResponse> deleteTheatre(@RequestParam("movieId") int movieId) {
		return movieService.deleteMovie(movieId);
	}

}
