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

import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.LocationResponse;
import com.movieticketbooking.entity.Location;
import com.movieticketbooking.service.LocationService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/location")
@CrossOrigin(origins = "http://localhost:3000")
public class LocationController {

	@Autowired
	private LocationService locationService;

	@PostMapping("/add")
	@Operation(summary = "Api to add location")
	public ResponseEntity<CommonApiResponse> addCategory(@RequestBody Location location) {
		return locationService.addLocation(location);
	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all location")
	public ResponseEntity<LocationResponse> fetchAllLocation() {
		return locationService.fetchAllLocation();
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete location and all its restaurants")
	public ResponseEntity<CommonApiResponse> deleteLocation(@RequestParam("locationId") int locationId) {
		return locationService.deleteLocation(locationId);
	}

}
