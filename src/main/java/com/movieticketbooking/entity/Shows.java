package com.movieticketbooking.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Shows {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private LocalDate showDate; // Example: 2025-04-08

	private LocalTime startTime; // Example: 18:30

	private LocalTime endTime; // Example: 21:15

	private String language; // Useful for multi-language shows (e.g., Hindi / Tamil)

	private String showType; // 2D / 3D / IMAX (optional)

	private String status; // ACTIVE / CANCELLED / COMPLETED / UPCOMING

	@ManyToOne
	@JoinColumn(name = "movie_id")
	private Movie movie;

	@ManyToOne
	@JoinColumn(name = "screen_id")
	private Screen screen;

	private BigDecimal goldSeatPrice;

	private BigDecimal regularSeatPrice;

	private BigDecimal premiumSeatPrice;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "theatre_id")
	private Theatre theatre;

}
