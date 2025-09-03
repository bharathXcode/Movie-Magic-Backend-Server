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

import com.movieticketbooking.dto.AddBookingRequest;
import com.movieticketbooking.dto.BookingResponse;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.ShowSeatBookingResponseDto;
import com.movieticketbooking.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/show/booking")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@PostMapping("add")
	@Operation(summary = "Api to add the show booking")
	public ResponseEntity<CommonApiResponse> addShowBooking(@RequestBody AddBookingRequest request) {
		return this.bookingService.addShowBooking(request);
	}

	@GetMapping("/fetch/theatre-wise")
	@Operation(summary = "Api to all show bookings by theatre wise")
	public ResponseEntity<BookingResponse> fetchShowBookingsByTheatre(@RequestParam("theatreId") Integer theatreId,
			@RequestParam("status") String status) {
		return bookingService.fetchShowBookingsByTheatre(theatreId, status);
	}

	@GetMapping("/fetch/customer-wise")
	@Operation(summary = "Api to all show bookings by theatre wise")
	public ResponseEntity<BookingResponse> fetchShowBookingsByCustomer(@RequestParam("customerId") Integer customerId,
			@RequestParam("status") String status) {
		return bookingService.fetchShowBookingsByCustomer(customerId, status);
	}

	// this api will help customer to select the seat and book the ticket
	@GetMapping("/fetch/show-wise")
	@Operation(summary = "Api to fetch all show bookings by show wise")
	public ResponseEntity<ShowSeatBookingResponseDto> fetchShowBookingsByShow(@RequestParam("showId") Integer showId) {
		return bookingService.fetchShowBookingsByShow(showId);
	}

	// for admin
	@GetMapping("/fetch")
	@Operation(summary = "Api to all show bookings")
	public ResponseEntity<BookingResponse> fetchShowBookingsByTheatre(@RequestParam("status") String status) {
		return bookingService.fetchShowBookingsByStatus(status);
	}
}
