package com.movieticketbooking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "show_id")
	private Shows show;

	@ManyToOne
    @JoinColumn(name = "show_seat_id")
    private ShowSeat showSeat;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private User customer;
	
	private String bookingTime;
	
	private String bookingId;
	
	private String status;
		
}
