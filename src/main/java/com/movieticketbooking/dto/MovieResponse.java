package com.movieticketbooking.dto;

import java.util.ArrayList;
import java.util.List;

import com.movieticketbooking.entity.Movie;

import lombok.Data;

@Data
public class MovieResponse extends CommonApiResponse {

	private List<Movie> movies = new ArrayList<>();

}
