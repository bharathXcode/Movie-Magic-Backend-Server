package com.movieticketbooking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.movieticketbooking.dao.BookingDao;
import com.movieticketbooking.dao.MovieDao;
import com.movieticketbooking.dao.ScreenDao;
import com.movieticketbooking.dao.ScreenSeatDao;
import com.movieticketbooking.dao.ShowDao;
import com.movieticketbooking.dao.ShowSeatDao;
import com.movieticketbooking.dao.TheatreDao;
import com.movieticketbooking.dao.UserDao;
import com.movieticketbooking.dto.AddShowRequest;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.ShowResponse;
import com.movieticketbooking.entity.Booking;
import com.movieticketbooking.entity.Movie;
import com.movieticketbooking.entity.Screen;
import com.movieticketbooking.entity.ScreenSeat;
import com.movieticketbooking.entity.ShowSeat;
import com.movieticketbooking.entity.Shows;
import com.movieticketbooking.entity.Theatre;
import com.movieticketbooking.entity.User;
import com.movieticketbooking.exception.ShowSaveFailedException;
import com.movieticketbooking.utility.Constants.BookingStatus;
import com.movieticketbooking.utility.Constants.SeatType;
import com.movieticketbooking.utility.Constants.ShowStatus;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShowService {

	private final Logger LOG = LoggerFactory.getLogger(ShowService.class);

	@Autowired
	private TheatreDao theatreDao;

	@Autowired
	private MovieDao movieDao;

	@Autowired
	private ShowDao showDao;

	@Autowired
	private ScreenDao screenDao;

	@Autowired
	private ScreenSeatDao screenSeatDao;

	@Autowired
	private ShowSeatDao showSeatDao;

	@Autowired
	private BookingDao bookingDao;

	@Autowired
	private UserDao userDao;

	public ResponseEntity<CommonApiResponse> addTheatreShow(AddShowRequest request) {

		LOG.info("Request received for adding the theatre show");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("request is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getTheatreId() == 0) {
			response.setResponseMessage("missing theatre id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getScreenId() == 0) {
			response.setResponseMessage("missing theatre screen id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getMovieId() == 0) {
			response.setResponseMessage("missing movie id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Theatre theatre = this.theatreDao.findById(request.getTheatreId()).orElse(null);

		if (theatre == null) {
			response.setResponseMessage("theatre not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Screen screen = this.screenDao.findById(request.getScreenId()).orElse(null);

		if (screen == null) {
			response.setResponseMessage("Screen not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Movie movie = this.movieDao.findById(request.getMovieId()).orElse(null);

		if (movie == null) {
			response.setResponseMessage("Movie not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Shows show = new Shows();
		show.setStatus(ShowStatus.ACTIVE.value());
		show.setShowDate(LocalDate.parse(request.getShowDate())); // converting String to LocalDate
		show.setStartTime(LocalTime.parse(request.getStartTime())); // converting String to LocalTime
		show.setEndTime(LocalTime.parse(request.getEndTime())); // converting String to LocalTime
		show.setLanguage(request.getLanguage());
		show.setShowType(request.getShowType());
		show.setGoldSeatPrice(request.getGoldSeatPrice());
		show.setRegularSeatPrice(request.getRegularSeatPrice());
		show.setPremiumSeatPrice(request.getPremiumSeatPrice());
		show.setScreen(screen);
		show.setTheatre(theatre);
		show.setMovie(movie);
		Shows savedShow = this.showDao.save(show);

		if (savedShow == null) {
			throw new ShowSaveFailedException("Failed to add the Theatre Show!!!");
		}

		generateShowSeats(savedShow, screen); // generate the Show Seat for the added show with the price

		generateInitialBookings(savedShow); // generate the Booking entries based on the Show Seats

		response.setResponseMessage("Theatre Show Added successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	private void generateShowSeats(Shows savedShow, Screen screen) {

		// Fetch all ScreenSeats for the given Screen
		List<ScreenSeat> screenSeats = this.screenSeatDao.findByScreen(screen);

		// List to hold ShowSeat objects
		List<ShowSeat> showSeatList = new ArrayList<>();

		for (ScreenSeat screenSeat : screenSeats) {
			ShowSeat showSeat = new ShowSeat();

			// Copy basic details from ScreenSeat
			showSeat.setSeatNumber(screenSeat.getSeatNumber());
			showSeat.setSeatType(screenSeat.getSeatType());
			showSeat.setSeatPosition(screenSeat.getSeatPosition());

			// Set the price based on seat type (coming from Show entity)
			if (screenSeat.getSeatType().equals(SeatType.REGULAR.value())) {
				showSeat.setPrice(savedShow.getRegularSeatPrice());
			} else if (screenSeat.getSeatType().equals(SeatType.PREMIUM.value())) {
				showSeat.setPrice(savedShow.getPremiumSeatPrice());
			} else if (screenSeat.getSeatType().equals(SeatType.GOLD.value())) {
				showSeat.setPrice(savedShow.getGoldSeatPrice());
			}

			// Set the Show
			showSeat.setShow(savedShow);

			// Add to list
			showSeatList.add(showSeat);
		}

		// Save all ShowSeats
		this.showSeatDao.saveAll(showSeatList);

		LOG.info("Generated {} seats for Show ID: {}", showSeatList.size(), savedShow.getId());
	}

	private void generateInitialBookings(Shows savedShow) {

		// Fetch all ShowSeats generated for the Show
		List<ShowSeat> showSeats = this.showSeatDao.findByShow(savedShow);

		List<Booking> bookingList = new ArrayList<>();

		for (ShowSeat showSeat : showSeats) {
			Booking booking = new Booking();

			booking.setShow(savedShow);
			booking.setShowSeat(showSeat);
			booking.setStatus(BookingStatus.AVAILABLE.value());
			booking.setBookingTime(""); // update this when customer book the show
			booking.setBookingId(""); // update this when customer book the show
			booking.setCustomer(null); // update this when customer book the show

			bookingList.add(booking);
		}

		this.bookingDao.saveAll(bookingList);

		LOG.info("Generated {} initial booking entries for Show ID: {}", bookingList.size(), savedShow.getId());
	}

	public ResponseEntity<CommonApiResponse> updateShowStatus(int showId, String status) {

		LOG.info("Request received for deleting the thatre screen");

		CommonApiResponse response = new CommonApiResponse();

		if (showId == 0 || status == null) {

			response.setResponseMessage("Missing input!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Shows show = this.showDao.findById(showId).orElse(null);

		if (show == null) {
			response.setResponseMessage("Theatre Show not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		show.setStatus(status);

		Shows savedShow = this.showDao.save(show);

		if (savedShow == null) {
			throw new ShowSaveFailedException("Failed to update the Theatre Show!!!");
		}

		// if show is cancelled then refund should be initiated
		if (status.equals(ShowStatus.CANCELLED.value())) {

			List<Booking> bookings = this.bookingDao.findByShowAndStatus(savedShow, BookingStatus.BOOKED.value());

			if (!CollectionUtils.isEmpty(bookings)) {
				for (Booking booking : bookings) {
					booking.setStatus(BookingStatus.CANCELLED.value());

					bookingDao.save(booking);

					User customer = userDao.findById(booking.getCustomer().getId()).get();
					User theatreManager = userDao.findById(booking.getShow().getTheatre().getManager().getId()).get();

					BigDecimal updatedCustomerWallet = customer.getWalletAmount().add(booking.getShowSeat().getPrice());
					BigDecimal updatedManagerWallet = theatreManager.getWalletAmount()
							.subtract(booking.getShowSeat().getPrice());

					customer.setWalletAmount(updatedCustomerWallet);
					theatreManager.setWalletAmount(updatedManagerWallet);

					this.userDao.save(customer);
					this.userDao.save(theatreManager);
				}
			}

		}

		response.setResponseMessage("Theatre Show Status updated successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<ShowResponse> fetchShowsByTheatre(Integer theatreId) {

		LOG.info("Request received for fetching shows by Theatre");

		ShowResponse response = new ShowResponse();

		if (theatreId == null || theatreId == 0) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<ShowResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Theatre theatre = this.theatreDao.findById(theatreId).orElse(null);

		if (theatre == null) {
			response.setResponseMessage("Theatre not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<ShowResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Shows> shows = this.showDao.findByTheatreOrderByIdDesc(theatre);

		if (CollectionUtils.isEmpty(shows)) {
			response.setResponseMessage("No shows found");
			response.setSuccess(false);

			return new ResponseEntity<ShowResponse>(response, HttpStatus.OK);
		}

		response.setShows(shows);
		response.setResponseMessage("Theatre Shows fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ShowResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<ShowResponse> fetchShowsByMovie(Integer movieId) {

		LOG.info("Request received for fetching shows by Theatre");

		ShowResponse response = new ShowResponse();

		if (movieId == null || movieId == 0) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<ShowResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Movie movie = this.movieDao.findById(movieId).orElse(null);

		if (movie == null) {
			response.setResponseMessage("Movie not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<ShowResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Shows> shows = this.showDao.findByMovie(movie);

		if (CollectionUtils.isEmpty(shows)) {
			response.setResponseMessage("No shows found");
			response.setSuccess(false);

			return new ResponseEntity<ShowResponse>(response, HttpStatus.OK);
		}

		response.setShows(shows);
		response.setResponseMessage("Theatre Shows fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ShowResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<ShowResponse> fetchUpcomingShowsByTheatre(Integer theatreId) {

		LOG.info("Request received for fetching upcoming shows by Theatre");

		ShowResponse response = new ShowResponse();

		if (theatreId == null || theatreId == 0) {
			response.setResponseMessage("Missing theatre id input!!!");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		Theatre theatre = this.theatreDao.findById(theatreId).orElse(null);

		if (theatre == null) {
			response.setResponseMessage("Theatre not found!!!");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();

		List<Shows> shows = this.showDao.findUpcomingShowsByTheatreAndStatus(theatre, today, now);

		if (CollectionUtils.isEmpty(shows)) {
			response.setResponseMessage("No upcoming shows found");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		List<Shows> upcomingShows = shows.stream().filter(show -> {
			if (show.getShowDate().isAfter(today)) {
				return true;
			} else if (show.getShowDate().isEqual(today)) {
				return show.getStartTime().isAfter(now) || show.getStartTime().equals(now);
			}
			return false;
		}).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(upcomingShows)) {
			response.setResponseMessage("No upcoming shows found");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		response.setShows(upcomingShows);
		response.setResponseMessage("Upcoming Theatre Shows fetched successfully");
		response.setSuccess(true);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ShowResponse> fetchUpcomingShowsByTheatreAndShowName(Integer theatreId, String showName) {

		LOG.info("Request received for fetching upcoming shows by Theatre");

		ShowResponse response = new ShowResponse();

		if (theatreId == null || theatreId == 0) {
			response.setResponseMessage("Missing theatre id input!!!");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		Theatre theatre = this.theatreDao.findById(theatreId).orElse(null);

		if (theatre == null) {
			response.setResponseMessage("Theatre not found!!!");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		if(showName == null) {
			response.setResponseMessage("Show name missing!!");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();

		List<Shows> shows = this.showDao.findUpcomingShowsByTheatreAndStatusAndShowNameLike(theatre, today, now, showName);

		if (CollectionUtils.isEmpty(shows)) {
			response.setResponseMessage("No upcoming shows found");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		List<Shows> upcomingShows = shows.stream().filter(show -> {
			if (show.getShowDate().isAfter(today)) {
				return true;
			} else if (show.getShowDate().isEqual(today)) {
				return show.getStartTime().isAfter(now) || show.getStartTime().equals(now);
			}
			return false;
		}).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(upcomingShows)) {
			response.setResponseMessage("No upcoming shows found");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		response.setShows(upcomingShows);
		response.setResponseMessage("Upcoming Theatre Shows fetched successfully");
		response.setSuccess(true);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ShowResponse> fetchAllShows() {

		LOG.info("Request received for fetching all shows");

		ShowResponse response = new ShowResponse();

		List<Shows> shows = this.showDao.findAllByOrderByIdDesc();

		if (CollectionUtils.isEmpty(shows)) {
			response.setResponseMessage("No shows found");
			response.setSuccess(false);

			return new ResponseEntity<ShowResponse>(response, HttpStatus.OK);
		}

		response.setShows(shows);
		response.setResponseMessage("Theatre Shows fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ShowResponse>(response, HttpStatus.OK);
	}

}
