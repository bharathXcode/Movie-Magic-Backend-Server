package com.movieticketbooking.entity;

import java.util.ArrayList;
import java.util.List;

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
public class Screen {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name; // Example: Screen A, B, C

	private int totalSeats;

	// below seat details are for dynamically showing the Seat Structure in UI to
	// the customer
	// note: pricing of seat we will keep based on the Show running in Shows Entity
	private int totalRegularSeats;

	private int totalRegularSeatRow;

	private int totalPremiumSeats;

	private int totalPremiumSeatRow;

	private int totalGoldSeats;

	private int totalGoldSeatRow;

	private int totalLeftSideSeats;

	private int totalRightSideSeats;

	private int totalMiddleSeats;

	@ManyToOne
	@JoinColumn(name = "theatre_id")
	private Theatre theatre;

	@OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ScreenSeat> screenSeats = new ArrayList<>();

	private String status;
}
