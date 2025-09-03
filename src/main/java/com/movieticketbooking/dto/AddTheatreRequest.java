package com.movieticketbooking.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AddTheatreRequest {

	private int theatreId;  // for theatre update
	
	private String name;

	private String address;

	private MultipartFile image;

	private String managerContact;

	private String emailId;

	private double latitude;

	private double longitude;

	private String description;

	private int locationId;

	private int managerId;

}
