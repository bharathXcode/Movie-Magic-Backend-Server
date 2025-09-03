package com.movieticketbooking.dto;

import lombok.Data;

@Data
public class UpdateTheatreDetailRequest {

	private int theatreId;
	
	private String name;

	private String address;

	private String managerContact;

	private String emailId;

	private String description;

}
