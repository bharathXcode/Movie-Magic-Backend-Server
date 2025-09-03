package com.movieticketbooking.entity;

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
public class ScreenSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String seatNumber; // A1, A2, B3 etc.

	private String seatType; // REGULAR, PREMIUM, GOLD

	private String seatPosition; // LEFT, RIGHT, MIDDLE

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "screen_id")
	private Screen screen;
}

