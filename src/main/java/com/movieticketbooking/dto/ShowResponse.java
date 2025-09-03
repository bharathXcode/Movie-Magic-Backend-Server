package com.movieticketbooking.dto;

import java.util.ArrayList;
import java.util.List;

import com.movieticketbooking.entity.Shows;

import lombok.Data;

@Data
public class ShowResponse extends CommonApiResponse {

	private List<Shows> shows = new ArrayList<>();

}
