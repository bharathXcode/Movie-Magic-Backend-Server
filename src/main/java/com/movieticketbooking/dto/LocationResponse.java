package com.movieticketbooking.dto;

import java.util.ArrayList;
import java.util.List;

import com.movieticketbooking.entity.Location;

import lombok.Data;

@Data
public class LocationResponse extends CommonApiResponse {

	private List<Location> locations = new ArrayList<>();

}
