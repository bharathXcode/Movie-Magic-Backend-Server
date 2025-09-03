package com.movieticketbooking.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String title;

	private String description;

	private String director;

	private String producer;

	private String cast; // comma-separated or can later be made into another table

	private String language;

	private String genre; // Action, Comedy, Drama, etc. — or enum/multi-select in future

	private String duration; // Format: "2h 15m" or "135 min"

	private String releaseDate;

	private String certification; // UA, A, U, etc.

	private String format; // 2D / 3D / IMAX — optional

	private String posterImage; // For showing image in frontend

	private String trailerUrl; // YouTube/Vimeo or local hosted

	private String status; // ACTIVE / INACTIVE / UPCOMING

	@JsonIgnore
	@OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
	private List<Shows> shows;
	
	@ManyToOne
	@JoinColumn(name = "theatre_id")
	private Theatre theatre;

}
