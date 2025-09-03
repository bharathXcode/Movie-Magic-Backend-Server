package com.movieticketbooking.dto;

import java.util.ArrayList;
import java.util.List;

import com.movieticketbooking.entity.Theatre;

import lombok.Data;

@Data
public class TheatreResponse extends CommonApiResponse {

	private List<Theatre> theatres = new ArrayList<>();

}
