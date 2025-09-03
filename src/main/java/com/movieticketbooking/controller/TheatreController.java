package com.movieticketbooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movieticketbooking.dto.AddTheatreRequest;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.TheatreResponse;
import com.movieticketbooking.dto.UpdateTheatreDetailRequest;
import com.movieticketbooking.service.TheatreService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/theatre")
@CrossOrigin(origins = "http://localhost:3000")
public class TheatreController {

	@Autowired
	private TheatreService theatreService;

	@PostMapping("add")
	@Operation(summary = "Api to add the theatre")
	public ResponseEntity<TheatreResponse> registerTheatre(AddTheatreRequest request) {
		return this.theatreService.registerTheatre(request);
	}

	@PutMapping("update/detail")
	@Operation(summary = "Api to update theatre details")
	public ResponseEntity<CommonApiResponse> updateTheatreDetails(@RequestBody UpdateTheatreDetailRequest request) {
		System.out.println(request);
		return this.theatreService .updateTheatreDetails(request);
	}

	@PutMapping("update/image")
	@Operation(summary = "Api to update theatre images")
	public ResponseEntity<CommonApiResponse> updateTheatreImage(AddTheatreRequest request) {
		return this.theatreService.updateTheatreImage(request);
	}

	@GetMapping("update/status")
	@Operation(summary = "Api to update the Theatre status")
	public ResponseEntity<TheatreResponse> updateTheatreStatus(@RequestParam("theatreId") int theatreId,
			@RequestParam("status") String status) {
		return this.theatreService.updateTheatreStatus(theatreId, status);
	}

	@GetMapping("/fetch/status-wise")
	@Operation(summary = "Api to all Theatres by status")
	public ResponseEntity<TheatreResponse> fetchTheatreByStatus(@RequestParam("status") String status) {
		return theatreService.fetchTheatreByStatus(status);
	}

	@GetMapping("/fetch/location-wise")
	@Operation(summary = "Api to all Theatres by location")
	public ResponseEntity<TheatreResponse> fetchTheatreByLocation(@RequestParam("locationId") Integer locationId) {
		return theatreService.fetchTheatreByLocation(locationId);
	}
	
	@GetMapping("/fetch/id-wise")
	@Operation(summary = "Api to all Theatres by Id")
	public ResponseEntity<TheatreResponse> fetchTheatreById(@RequestParam("theatreId") Integer theatreId) {
		return theatreService.fetchTheatreById(theatreId);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete the theatre and all movies and shows")
	public ResponseEntity<CommonApiResponse> deleteTheatre(@RequestParam("theatreId") int theatreId) {
		return theatreService.deleteTheatre(theatreId);
	}
	
	@GetMapping(value = "/{imageName}", produces = "image/*")
	public void fetchFoodImage(@PathVariable("imageName") String imageName, HttpServletResponse resp) {

		this.theatreService.fetchImage(imageName, resp);

	}

}
