package com.movieticketbooking.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.movieticketbooking.dao.BookingDao;
import com.movieticketbooking.dao.LocationDao;
import com.movieticketbooking.dao.MovieDao;
import com.movieticketbooking.dao.ShowDao;
import com.movieticketbooking.dao.TheatreDao;
import com.movieticketbooking.dao.UserDao;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.LocationResponse;
import com.movieticketbooking.entity.Booking;
import com.movieticketbooking.entity.Location;
import com.movieticketbooking.entity.Movie;
import com.movieticketbooking.entity.Shows;
import com.movieticketbooking.entity.Theatre;
import com.movieticketbooking.entity.User;
import com.movieticketbooking.exception.LocationSaveFailedException;
import com.movieticketbooking.utility.Constants.ActiveStatus;
import com.movieticketbooking.utility.Constants.BookingStatus;
import com.movieticketbooking.utility.Constants.ShowStatus;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class LocationService {

	private final Logger LOG = LoggerFactory.getLogger(LocationService.class);

	@Autowired
	private LocationDao locationDao;

	@Autowired
	private TheatreDao theatreDao;

	@Autowired
	private MovieDao movieDao;

	@Autowired
	private ShowDao showDao;

	@Autowired
	private BookingDao bookingDao;

	@Autowired
	private UserDao userDao;

	public ResponseEntity<CommonApiResponse> addLocation(Location location) {

		LOG.info("Request received for add location");

		CommonApiResponse response = new CommonApiResponse();

		if (location == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		location.setStatus(ActiveStatus.ACTIVE.value());

		Location savedLocation = this.locationDao.save(location);

		if (savedLocation == null) {
			throw new LocationSaveFailedException("Failed to add location");
		}

		response.setResponseMessage("Location Added Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<LocationResponse> fetchAllLocation() {

		LOG.info("Request received for fetching all locations");

		LocationResponse response = new LocationResponse();

		List<Location> locations = new ArrayList<>();

		locations = this.locationDao.findByStatus(ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(locations)) {
			response.setResponseMessage("No Locations found");
			response.setSuccess(false);

			return new ResponseEntity<LocationResponse>(response, HttpStatus.OK);
		}

		response.setLocations(locations);
		response.setResponseMessage("Locations fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<LocationResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteLocation(int locationId) {

		LOG.info("Request received for deleting location");

		CommonApiResponse response = new CommonApiResponse();

		if (locationId == 0) {
			response.setResponseMessage("missing location Id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Location location = this.locationDao.findById(locationId).orElse(null);

		if (location == null) {
			response.setResponseMessage("location not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Theatre> theatres = this.theatreDao.findByLocationAndStatus(location, ActiveStatus.ACTIVE.value());

		location.setStatus(ActiveStatus.DEACTIVATED.value());
		Location updatedLocation = this.locationDao.save(location);

		if (updatedLocation == null) {
			throw new LocationSaveFailedException("Failed to delete the location");
		}

		if (!CollectionUtils.isEmpty(theatres)) {

			for (Theatre theatre : theatres) {
				theatre.setStatus(ActiveStatus.DEACTIVATED.value());

				List<Movie> movies = this.movieDao.findByTheatreAndStatus(theatre, ActiveStatus.ACTIVE.value());

				if (!CollectionUtils.isEmpty(movies)) {
					for (Movie movie : movies) {
						movie.setStatus(ActiveStatus.DEACTIVATED.value());
					}

					movieDao.saveAll(movies);
				}

				List<Shows> shows = this.showDao.findByStatusAndTheatre(ActiveStatus.ACTIVE.value(), theatre);

				if (!CollectionUtils.isEmpty(shows)) {
					for (Shows show : shows) {
						show.setStatus(ShowStatus.CANCELLED.value());

						List<Booking> bookings = this.bookingDao.findByShowAndStatus(show,
								BookingStatus.BOOKED.value());

						if (!CollectionUtils.isEmpty(bookings)) {
							for (Booking booking : bookings) {
								booking.setStatus(BookingStatus.CANCELLED.value());

								User customer = userDao.findById(booking.getCustomer().getId()).get();
								User theatreManager = userDao
										.findById(booking.getShow().getTheatre().getManager().getId()).get();

								BigDecimal updatedCustomerWallet = customer.getWalletAmount()
										.add(booking.getShowSeat().getPrice());
								BigDecimal updatedManagerWallet = theatreManager.getWalletAmount()
										.subtract(booking.getShowSeat().getPrice());

								customer.setWalletAmount(updatedCustomerWallet);
								theatreManager.setWalletAmount(updatedManagerWallet);

								this.userDao.save(customer);
								this.userDao.save(theatreManager);
								bookingDao.save(booking);
							}
						}
					}

					showDao.saveAll(shows);
				}

				theatreDao.save(theatre);

			}

		}

		response.setResponseMessage("Location Deleted Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

}
