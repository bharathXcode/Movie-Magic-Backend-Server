package com.movieticketbooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movieticketbooking.dto.AddScreenRequest;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.ScreenResponse;
import com.movieticketbooking.service.ScreenService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/theatre/screen")
@CrossOrigin(origins = "http://localhost:3000")
public class ScreenController {

	@Autowired
	private ScreenService screenService;

	@PostMapping("add")
	@Operation(summary = "Api to add the theatre screen")
	public ResponseEntity<CommonApiResponse> addTheatreScreen(@RequestBody AddScreenRequest request) {
		return this.screenService.addTheatreScreen(request);
	}

	@GetMapping("/fetch/theatre-wise")
	@Operation(summary = "Api to fetch the screens by theatre")
	public ResponseEntity<ScreenResponse> fetchScreenByTheatre(@RequestParam("theatreId") Integer theatreId) {
		return screenService.fetchScreenByTheatre(theatreId);
	}
	
	@GetMapping("/fetch/status-wise")
	@Operation(summary = "Api to fetch the screens by status")
	public ResponseEntity<ScreenResponse> fetchScreenByTheatre(@RequestParam("status") String status) {
		return screenService.fetchScreensByStatus(status);
	}
	
	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete the theatre screens")
	public ResponseEntity<CommonApiResponse> deleteTheatreScreen(@RequestParam("screenId") int screenId) {
		return screenService.deleteTheatreScreen(screenId);
	}

	
}
