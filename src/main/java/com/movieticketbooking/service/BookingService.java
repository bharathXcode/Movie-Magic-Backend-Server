package com.movieticketbooking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.movieticketbooking.dao.BookingDao;
import com.movieticketbooking.dao.ShowDao;
import com.movieticketbooking.dao.TheatreDao;
import com.movieticketbooking.dao.UserDao;
import com.movieticketbooking.dto.AddBookingRequest;
import com.movieticketbooking.dto.BookingResponse;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.ShowSeatBookingData;
import com.movieticketbooking.dto.ShowSeatBookingResponseDto;
import com.movieticketbooking.entity.Booking;
import com.movieticketbooking.entity.Shows;
import com.movieticketbooking.entity.Theatre;
import com.movieticketbooking.entity.User;
import com.movieticketbooking.exception.BookingNotFoundException;
import com.movieticketbooking.exception.UserNotFoundException;
import com.movieticketbooking.utility.BookingIdGenerator;
import com.movieticketbooking.utility.Constants.BookingStatus;
import com.movieticketbooking.utility.EmailService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookingService {

	private final Logger LOG = LoggerFactory.getLogger(BookingService.class);

	@Autowired
	private BookingDao bookingDao;

	@Autowired
	private TheatreDao theatreDao;

	@Autowired
	private ShowDao showDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private EmailService emailService;

	public ResponseEntity<CommonApiResponse> addShowBooking(AddBookingRequest request) {

		LOG.info("Request received for adding the show booking");

		CommonApiResponse response = new CommonApiResponse();

		String bookingTime = String
				.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		if (request == null) {
			response.setResponseMessage("request is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (StringUtils.isEmpty(request.getBookingIds())) {
			response.setResponseMessage("missing booking ids");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getCustomerId() == 0) {
			response.setResponseMessage("missing customer id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User customer = this.userDao.findById(request.getCustomerId()).orElse(null);

		if (customer == null) {
			response.setResponseMessage("Customer not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String[] bookingIds = request.getBookingIds().split(",");

		// Fetch all bookings in one go using Stream
		List<Booking> bookings = Arrays.stream(bookingIds)
				.map(id -> this.bookingDao.findById(Integer.parseInt(id)).orElse(null)).peek(booking -> {
					if (booking == null) {
						throw new BookingNotFoundException("Selected Booking Entry not found in DB!!!!");
					}
				}).collect(Collectors.toList());

		// Check if any booking is not available
		boolean anyNotAvailable = bookings.stream()
				.anyMatch(booking -> !booking.getStatus().equals(BookingStatus.AVAILABLE.value()));

		if (anyNotAvailable) {
			response.setResponseMessage("Some of the selected seats are already booked!!!");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		BigDecimal totalSeatPrice = bookings.stream().map(b -> b.getShowSeat().getPrice()).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		if (customer.getWalletAmount().compareTo(totalSeatPrice) < 0) {
			response.setResponseMessage("Booking Failed, Insufficient Fund in your Wallet!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		// here we have to update the Booking entry, by adding the Customer, booking
		// time and bookingId generate
		// com.movieticketbooking.utility.BookingIdGenerator.generateBookingId() use
		// this
		// Generate common booking ID for this booking
		String bookingUniqueId = BookingIdGenerator.generateBookingId();

		// Update each booking
		bookings.forEach(booking -> {
			booking.setCustomer(customer);
			booking.setBookingId(bookingUniqueId);
			booking.setBookingTime(bookingTime);
			booking.setStatus(BookingStatus.BOOKED.value());
			this.bookingDao.save(booking);
		});

		// Deduct the total price from customer wallet
		customer.setWalletAmount(customer.getWalletAmount().subtract(totalSeatPrice));
		this.userDao.save(customer);

		// add the show seat price deducted from customer to the thearer manager wallet
		User theatreManager = this.userDao.findById(bookings.get(0).getShow().getTheatre().getManager().getId())
				.orElseThrow(() -> new UserNotFoundException("Theatre Manager not found!!"));

		theatreManager.setWalletAmount(theatreManager.getWalletAmount().add(totalSeatPrice));
		this.userDao.save(theatreManager);
		
		try {
			String mailBody = emailService.getMailBody(customer, bookings, bookingUniqueId);
			
			String subject = "Your Movie Magic Booking Confirmation – Booking ID: " + bookingUniqueId;

			this.emailService.sendEmail(customer.getEmailId(), subject, mailBody);   
		} catch (Exception e) {
			e.printStackTrace();
		}

		response.setResponseMessage("Congratulations! Your show booking has been successfully completed!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<BookingResponse> fetchShowBookingsByStatus(String status) {

		LOG.info("Request received for fetching show bookings by status");

		BookingResponse response = new BookingResponse();

		if (status == null) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Booking> bookings = this.bookingDao.findByStatus(status);

		if (CollectionUtils.isEmpty(bookings)) {
			response.setResponseMessage("No Bookings!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
		}

		response.setBookings(bookings);
		response.setResponseMessage("Bookings fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<BookingResponse> fetchShowBookingsByTheatre(Integer theatreId, String status) {

		LOG.info("Request received for fetching show bookings by status");

		BookingResponse response = new BookingResponse();

		if (status == null) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (theatreId == null || theatreId == 0) {
			response.setResponseMessage("Missing thetare id!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Theatre theatre = this.theatreDao.findById(theatreId).orElse(null);

		if (theatre == null) {
			response.setResponseMessage("Theatre not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Booking> bookings = this.bookingDao.findByTheatreAndStatus(theatre, status);

		if (CollectionUtils.isEmpty(bookings)) {
			response.setResponseMessage("No Bookings!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
		}

		response.setBookings(bookings);
		response.setResponseMessage("Bookings fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<BookingResponse> fetchShowBookingsByCustomer(Integer customerId, String status) {

		LOG.info("Request received for fetching show bookings by status");

		BookingResponse response = new BookingResponse();

		if (status == null) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (customerId == null || customerId == 0) {
			response.setResponseMessage("Missing thetare id!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User customer = this.userDao.findById(customerId).orElse(null);

		if (customer == null) {
			response.setResponseMessage("Theatre not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Booking> bookings = this.bookingDao.findByCustomerAndStatus(customer, status);

		if (CollectionUtils.isEmpty(bookings)) {
			response.setResponseMessage("No Bookings!!!");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
		}

		response.setBookings(bookings);
		response.setResponseMessage("Bookings fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<ShowSeatBookingResponseDto> fetchShowBookingsByShow(Integer showId) {

		LOG.info("Request received for fetching show bookings by status");

		ShowSeatBookingResponseDto response = new ShowSeatBookingResponseDto();

		if (showId == null || showId == 0) {
			response.setResponseMessage("Missing show id!!!");
			response.setSuccess(false);

			return new ResponseEntity<ShowSeatBookingResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Shows show = this.showDao.findById(showId).orElse(null);

		if (show == null) {
			response.setResponseMessage("Show not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<ShowSeatBookingResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Booking> bookings = this.bookingDao.findByShow(show);

		if (CollectionUtils.isEmpty(bookings)) {
			response.setResponseMessage("No Bookings!!!");
			response.setSuccess(false);

			return new ResponseEntity<ShowSeatBookingResponseDto>(response, HttpStatus.OK);
		}

		List<ShowSeatBookingData> showBookings = new ArrayList<>();

		for (Booking booking : bookings) {
			ShowSeatBookingData showBooking = new ShowSeatBookingData();
			showBooking.setId(booking.getId());
			showBooking.setPrice(booking.getShowSeat().getPrice());
			showBooking.setSeatNumber(booking.getShowSeat().getSeatNumber());
			showBooking.setSeatPosition(booking.getShowSeat().getSeatPosition());
			showBooking.setSeatType(booking.getShowSeat().getSeatType());
			showBooking.setStatus(booking.getStatus());

			showBookings.add(showBooking);
		}

		response.setBookings(showBookings);
		response.setResponseMessage("Bookings fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ShowSeatBookingResponseDto>(response, HttpStatus.OK);
	}

}
