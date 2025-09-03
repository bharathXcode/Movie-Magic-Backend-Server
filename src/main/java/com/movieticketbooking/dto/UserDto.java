package com.movieticketbooking.dto;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;

import com.movieticketbooking.entity.Theatre;
import com.movieticketbooking.entity.User;

import lombok.Data;

@Data
public class UserDto {

	private int id;

	private String firstName;

	private String lastName;

	private String emailId;

	private String phoneNo;

	private String role;

	private String status;
	
	private Theatre theatre;
	
	private BigDecimal walletAmount;

	public static UserDto toUserDtoEntity(User user) {
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(user, userDto);
		return userDto;
	}

}
