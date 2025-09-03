package com.movieticketbooking.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Theatre {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private String address;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "location_id")
	private Location location;

	private String image;

	private String managerContact;

	private String emailId;

	private double latitude;

	private double longitude;

	private String status;

	private String description;

	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "manager_id", referencedColumnName = "id")
	private User manager;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Theatre that = (Theatre) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
