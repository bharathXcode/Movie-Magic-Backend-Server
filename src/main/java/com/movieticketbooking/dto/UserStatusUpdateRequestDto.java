package com.movieticketbooking.dto;

import lombok.Data;

@Data
public class UserStatusUpdateRequestDto { 
	
	private int userId;
	
	private String status;

}
