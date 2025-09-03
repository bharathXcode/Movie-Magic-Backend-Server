package com.movieticketbooking.dto;

import java.util.ArrayList;
import java.util.List;

import com.movieticketbooking.entity.Screen;

import lombok.Data;

@Data
public class ScreenResponse extends CommonApiResponse {

	List<Screen> screens = new ArrayList<>();
	
}
