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

import com.movieticketbooking.dto.AddShowRequest;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.ShowResponse;
import com.movieticketbooking.service.ShowService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/theatre/show")
@CrossOrigin(origins = "http://localhost:3000")
public class ShowController {

	@Autowired
	private ShowService showService;

	@PostMapping("add")
	@Operation(summary = "Api to add the theatre show")
	public ResponseEntity<CommonApiResponse> addTheatreShow(@RequestBody AddShowRequest request) {
		return this.showService.addTheatreShow(request);
	}

	// for theatre manager
	@GetMapping("/fetch/theatre-wise")
	@Operation(summary = "Api to fetch the shows by theatre")
	public ResponseEntity<ShowResponse> fetchShowsByTheatre(@RequestParam("theatreId") Integer theatreId) {
		return showService.fetchShowsByTheatre(theatreId);
	}

	@GetMapping("/fetch/movie-wise")
	@Operation(summary = "Api to fetch the Shows based on Theatre Movie")
	public ResponseEntity<ShowResponse> fetchShowsByMovie(@RequestParam("movieId") Integer movieId) {
		return showService.fetchShowsByMovie(movieId);
	}

	@GetMapping("/update/status")
	@Operation(summary = "Api to update the show status")
	public ResponseEntity<CommonApiResponse> fetchScreenByTheatre(@RequestParam("showId") int showId,
			@RequestParam("status") String status) {
		return showService.updateShowStatus(showId, status);
	}
	
	// show all the theatre shows for the future date for customers
	@GetMapping("/fetch/theatre-wise/upcoming")
	@Operation(summary = "Api to fetch the upcoming shows by theatre")
	public ResponseEntity<ShowResponse> fetchUpcomingShowsByTheatre(@RequestParam("theatreId") Integer theatreId) {
		return showService.fetchUpcomingShowsByTheatre(theatreId);
	}
	
	@GetMapping("/fetch/showname-wise/upcoming")
	@Operation(summary = "Api to fetch the upcoming shows by theatre")
	public ResponseEntity<ShowResponse> fetchUpcomingShowsByTheatre(@RequestParam("theatreId") Integer theatreId,
			@RequestParam("showName") String showName) {
		return showService.fetchUpcomingShowsByTheatreAndShowName(theatreId, showName);
	}
	
	// for admin
	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch the shows by theatre")
	public ResponseEntity<ShowResponse> fetchAllShows() {
		return showService.fetchAllShows();
	}

}
