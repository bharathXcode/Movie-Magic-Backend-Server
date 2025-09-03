package com.movieticketbooking.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.movieticketbooking.dao.BookingDao;
import com.movieticketbooking.dao.ScreenDao;
import com.movieticketbooking.dao.ScreenSeatDao;
import com.movieticketbooking.dao.ShowDao;
import com.movieticketbooking.dao.TheatreDao;
import com.movieticketbooking.dao.UserDao;
import com.movieticketbooking.dto.AddScreenRequest;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.ScreenResponse;
import com.movieticketbooking.entity.Booking;
import com.movieticketbooking.entity.Screen;
import com.movieticketbooking.entity.ScreenSeat;
import com.movieticketbooking.entity.Shows;
import com.movieticketbooking.entity.Theatre;
import com.movieticketbooking.entity.User;
import com.movieticketbooking.exception.ScreenSaveFailedException;
import com.movieticketbooking.utility.Constants.ActiveStatus;
import com.movieticketbooking.utility.Constants.BookingStatus;
import com.movieticketbooking.utility.Constants.ScreenSeatPosition;
import com.movieticketbooking.utility.Constants.SeatType;
import com.movieticketbooking.utility.Constants.ShowStatus;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ScreenService {

	private final Logger LOG = LoggerFactory.getLogger(ScreenService.class);

	@Autowired
	private TheatreDao theatreDao;

	@Autowired
	private ScreenDao screenDao;

	@Autowired
	private ScreenSeatDao screenSeatDao;

	@Autowired
	private ShowDao showDao;
	
	@Autowired
	private BookingDao bookingDao;
	
	@Autowired
	private UserDao userDao;
	
	public ResponseEntity<CommonApiResponse> addTheatreScreen(AddScreenRequest request) {

		LOG.info("Request received for adding the screen");

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

		Theatre theatre = this.theatreDao.findById(request.getTheatreId()).orElse(null);

		if (theatre == null) {
			response.setResponseMessage("theatre not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Screen screen = new Screen();
		screen.setName(request.getName());
		screen.setTotalSeats(request.getTotalSeats());
		screen.setTotalRegularSeats(request.getTotalRegularSeats());
		screen.setTotalPremiumSeats(request.getTotalPremiumSeats());
		screen.setTotalGoldSeats(request.getTotalGoldSeats());
		screen.setTotalLeftSideSeats(request.getTotalLeftSideSeats());
		screen.setTotalRightSideSeats(request.getTotalRightSideSeats());
		screen.setTotalMiddleSeats(request.getTotalMiddleSeats());
		screen.setTotalGoldSeatRow(request.getTotalGoldSeatRow());
		screen.setTotalPremiumSeatRow(request.getTotalPremiumSeatRow());
		screen.setTotalRegularSeatRow(request.getTotalRegularSeatRow());
		screen.setTheatre(theatre);
		screen.setStatus(ActiveStatus.ACTIVE.value());

		Screen savedScreen = this.screenDao.save(screen);

		if (savedScreen == null) {
			throw new ScreenSaveFailedException("Failed to add the Theatre Screen!!!");
		}

		generateScreenSeats(savedScreen, request); // generate the Screen Seat for the added Screen

		response.setResponseMessage("Theatre Screen Added successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	private void generateScreenSeats(Screen savedScreen, AddScreenRequest request) {

		// Calculate total number of seat rows (sum of Regular + Premium + Gold rows)
		int totalSeatRows = request.getTotalRegularSeatRow() + request.getTotalPremiumSeatRow()
				+ request.getTotalGoldSeatRow();

		// Create a list to hold all the generated ScreenSeat objects
		List<ScreenSeat> screenSeatList = new ArrayList<>();

		int seatCounter; // To track seat number inside a row (1, 2, 3, etc.)
		char rowChar = 'A'; // Start seat rows with 'A' (then B, C, D...)

		// Loop for each row (from 0 to totalSeatRows-1)
		for (int row = 0; row < totalSeatRows; row++) {

			// Determine the Seat Type (REGULAR / PREMIUM / GOLD) based on row number
			String seatType;
			if (row < request.getTotalRegularSeatRow()) {
				seatType = SeatType.REGULAR.value(); // First few rows are Regular
			} else if (row < request.getTotalRegularSeatRow() + request.getTotalPremiumSeatRow()) {
				seatType = SeatType.PREMIUM.value(); // Then next few rows are Premium
			} else {
				seatType = SeatType.GOLD.value(); // Remaining rows are Gold
			}

			seatCounter = 1; // Reset seat counter for each new row

			// -------- Generate LEFT side seats --------
			for (int i = 0; i < request.getTotalLeftSideSeats(); i++) {
				ScreenSeat seat = new ScreenSeat();
				seat.setSeatNumber(rowChar + String.valueOf(seatCounter)); // Example: A1, A2, etc.
				seat.setSeatType(seatType); // REGULAR / PREMIUM / GOLD
				seat.setSeatPosition(ScreenSeatPosition.LEFT.value()); // Mark as LEFT
				seat.setScreen(savedScreen); // Set which screen it belongs to
				screenSeatList.add(seat); // Add to list
				seatCounter++; // Move to next seat number
			}

			// -------- Generate MIDDLE seats --------
			for (int i = 0; i < request.getTotalMiddleSeats(); i++) {
				ScreenSeat seat = new ScreenSeat();
				seat.setSeatNumber(rowChar + String.valueOf(seatCounter)); // Example: A5, A6, etc.
				seat.setSeatType(seatType);
				seat.setSeatPosition(ScreenSeatPosition.MIDDLE.value()); // Mark as MIDDLE
				seat.setScreen(savedScreen);
				screenSeatList.add(seat);
				seatCounter++;
			}

			// -------- Generate RIGHT side seats --------
			for (int i = 0; i < request.getTotalRightSideSeats(); i++) {
				ScreenSeat seat = new ScreenSeat();
				seat.setSeatNumber(rowChar + String.valueOf(seatCounter)); // Example: A10, A11, etc.
				seat.setSeatType(seatType);
				seat.setSeatPosition(ScreenSeatPosition.RIGHT.value()); // Mark as RIGHT
				seat.setScreen(savedScreen);
				screenSeatList.add(seat);
				seatCounter++;
			}

			// Move to the next row (example: A -> B -> C ...)
			rowChar++;
		}

		// Save all the generated seats into the database at once
		this.screenSeatDao.saveAll(screenSeatList);

		// Log how many seats were generated for this screen
		LOG.info("Generated {} seats for Screen: {}", screenSeatList.size(), savedScreen.getName());
	}

	public ResponseEntity<ScreenResponse> fetchScreenByTheatre(Integer theatreId) {

		LOG.info("Request received for fetching the Theatre screens");

		ScreenResponse response = new ScreenResponse();

		if (theatreId == null || theatreId == 0) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<ScreenResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Theatre theatre = this.theatreDao.findById(theatreId).orElse(null);

		if (theatre == null) {
			response.setResponseMessage("Theatre not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<ScreenResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Screen> screens = this.screenDao.findByStatusAndTheatre(ActiveStatus.ACTIVE.value(), theatre);

		if (CollectionUtils.isEmpty(screens)) {
			response.setResponseMessage("No Theatre Screens found");
			response.setSuccess(false);

			return new ResponseEntity<ScreenResponse>(response, HttpStatus.OK);
		}

		response.setScreens(screens);

		response.setResponseMessage("Theatre Screens fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ScreenResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<ScreenResponse> fetchScreensByStatus(String status) {

		LOG.info("Request received for fetching the Theatre screens");

		ScreenResponse response = new ScreenResponse();

		if (status == null) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<ScreenResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Screen> screens = this.screenDao.findByStatus(ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(screens)) {
			response.setResponseMessage("No Theatre Screens found");
			response.setSuccess(false);

			return new ResponseEntity<ScreenResponse>(response, HttpStatus.OK);
		}

		response.setScreens(screens);

		response.setResponseMessage("Theatre Screens fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ScreenResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteTheatreScreen(int screenId) {

		LOG.info("Request received for deleting the thatre screen");

		CommonApiResponse response = new CommonApiResponse();

		if (screenId == 0) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Screen screen = this.screenDao.findById(screenId).orElse(null);

		if (screen == null) {
			response.setResponseMessage("Theatre Screen not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		screen.setStatus(ActiveStatus.DEACTIVATED.value());
		
		List<Shows> shows = this.showDao.findByStatusAndScreen(ShowStatus.ACTIVE.value(), screen);

		if (!CollectionUtils.isEmpty(shows)) {
			for (Shows show : shows) {
				show.setStatus(ShowStatus.CANCELLED.value());
				
				List<Booking> bookings = this.bookingDao.findByShowAndStatus(show, BookingStatus.BOOKED.value());

				if (!CollectionUtils.isEmpty(bookings)) {
					for (Booking booking : bookings) {
						booking.setStatus(BookingStatus.CANCELLED.value());

						User customer = userDao.findById(booking.getCustomer().getId()).get();
						User theatreManager = userDao.findById(booking.getShow().getTheatre().getManager().getId())
								.get();

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

		response.setResponseMessage("Theatre Screen Deleted Succesful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<ScreenResponse> fetchScreenByStatus(String status) {
		// TODO Auto-generated method stub
		return null;
	}

}
