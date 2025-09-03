package com.movieticketbooking.dto;

import lombok.Data;

@Data
public class UpdateMovieDetailRequest {

	private int movieId;

	private String title;

	private String description;

	private String director;

	private String producer;

	private String cast; // comma-separated or can later be made into another table

	private String language;

	private String genre; // Action, Comedy, Drama, etc. — or enum/multi-select in future

	private String duration; // Format: "2h 15m" or "135 min"

	private String releaseDate;

	private String certification; // UA, A, U, S etc.

	private String format; // 2D / 3D / IMAX — optional

	private String trailerUrl; // YouTube/Vimeo or local hosted

}
