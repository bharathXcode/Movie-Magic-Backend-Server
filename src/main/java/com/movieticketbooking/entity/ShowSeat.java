package com.movieticketbooking.entity;

import java.math.BigDecimal;

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
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String seatNumber;  // Example: A1, B2

    private String seatType;    // REGULAR, PREMIUM, GOLD

    private String seatPosition; // Left, Right, Middle

    private BigDecimal price;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "show_id")
    private Shows show;

}
