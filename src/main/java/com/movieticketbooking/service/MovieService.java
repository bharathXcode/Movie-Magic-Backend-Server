package com.movieticketbooking.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.movieticketbooking.dao.BookingDao;
import com.movieticketbooking.dao.MovieDao;
import com.movieticketbooking.dao.ShowDao;
import com.movieticketbooking.dao.TheatreDao;
import com.movieticketbooking.dao.UserDao;
import com.movieticketbooking.dto.AddMovieRequest;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.MovieResponse;
import com.movieticketbooking.dto.UpdateMovieDetailRequest;
import com.movieticketbooking.entity.Booking;
import com.movieticketbooking.entity.Movie;
import com.movieticketbooking.entity.Shows;
import com.movieticketbooking.entity.Theatre;
import com.movieticketbooking.entity.User;
import com.movieticketbooking.exception.MovieSaveFailedException;
import com.movieticketbooking.utility.Constants.ActiveStatus;
import com.movieticketbooking.utility.Constants.BookingStatus;
import com.movieticketbooking.utility.Constants.ShowStatus;
import com.movieticketbooking.utility.StorageService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MovieService {

	private final Logger LOG = LoggerFactory.getLogger(MovieService.class);

	@Autowired
	private TheatreDao theatreDao;

	@Autowired
	private StorageService storageService;

	@Autowired
	private MovieDao movieDao;
	
	@Autowired
	private ShowDao showDao;
	
	@Autowired
	private BookingDao bookingDao;
	
	@Autowired
	private UserDao userDao;

	public ResponseEntity<CommonApiResponse> addTheatreMovie(AddMovieRequest request) {

		LOG.info("Request received for adding the theatre movie");

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

		String posterImage = this.storageService.store(request.getImage());

		Movie movie = new Movie();
		movie.setTitle(request.getTitle());
		movie.setDescription(request.getDescription());
		movie.setDirector(request.getDirector());
		movie.setProducer(request.getProducer());
		movie.setCast(request.getCast());
		movie.setLanguage(request.getLanguage());
		movie.setGenre(request.getGenre());
		movie.setDuration(request.getDuration());
		movie.setReleaseDate(request.getReleaseDate());
		movie.setCertification(request.getCertification());
		movie.setFormat(request.getFormat());
		movie.setPosterImage(posterImage);
		movie.setTrailerUrl(request.getTrailerUrl());
		movie.setTheatre(theatre);
		movie.setStatus(ActiveStatus.ACTIVE.value());

		Movie savedMovie = this.movieDao.save(movie);

		if (savedMovie == null) {
			throw new MovieSaveFailedException("Failed to add the Theatre Movie!!!");
		}

		response.setResponseMessage("Movie Added successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateTheatreMovieDetails(UpdateMovieDetailRequest request) {

		LOG.info("Request received for adding the theatre movie");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("request is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getMovieId() == 0) {
			response.setResponseMessage("missing movie id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Movie movie = this.movieDao.findById(request.getMovieId()).orElse(null);

		if (movie == null) {
			response.setResponseMessage("movie not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		movie.setTitle(request.getTitle());
		movie.setDescription(request.getDescription());
		movie.setDirector(request.getDirector());
		movie.setProducer(request.getProducer());
		movie.setCast(request.getCast());
		movie.setLanguage(request.getLanguage());
		movie.setGenre(request.getGenre());
		movie.setDuration(request.getDuration());
		movie.setReleaseDate(request.getReleaseDate());
		movie.setCertification(request.getCertification());
		movie.setFormat(request.getFormat());
		movie.setTrailerUrl(request.getTrailerUrl());
		movie.setStatus(ActiveStatus.ACTIVE.value());

		Movie savedMovie = this.movieDao.save(movie);

		if (savedMovie == null) {
			throw new MovieSaveFailedException("Failed to update the Theatre Movie!!!");
		}

		response.setResponseMessage("Movie Updated successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateTheatreImage(AddMovieRequest request) {

		LOG.info("request received for update movie poster image");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getMovieId() == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getImage() == null) {
			response.setResponseMessage("Poster not selected");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Movie movie = this.movieDao.findById(request.getMovieId()).orElse(null);

		if (movie == null) {
			response.setResponseMessage("movie not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String existingImage = movie.getPosterImage();

		// store updated theatre image in Image Folder and give name to store in
		// database
		String newTheatreImageName = storageService.store(request.getImage());

		movie.setPosterImage(newTheatreImageName);

		Movie savedMovie = this.movieDao.save(movie);

		if (savedMovie == null) {
			throw new MovieSaveFailedException("Failed to update the Movie poster image!!!");
		}

		// deleting the existing image from the folder
		try {
			this.storageService.delete(existingImage);

		} catch (Exception e) {
			LOG.error("Exception Caught: " + e.getMessage());

			throw new MovieSaveFailedException("Failed to update the Movie Poster image");
		}

		response.setResponseMessage("Movie Poster Updated Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CommonApiResponse> updateMovieStatus(int movieId, String status) {

		LOG.info("Request received for updating the theatre");

		CommonApiResponse response = new CommonApiResponse();

		if (movieId == 0 || status == null) {
			response.setResponseMessage("Missing input!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Movie movie = this.movieDao.findById(movieId).orElse(null);

		if (movie == null) {
			response.setResponseMessage("movie not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		movie.setStatus(status);

		Movie savedMovie = this.movieDao.save(movie);

		if (savedMovie == null) {
			throw new MovieSaveFailedException("Failed to update the Movie!!!");
		}

		response.setResponseMessage("Movie Status updated successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<MovieResponse> fetchAllMoviesByStatus(String status) {

		LOG.info("Request received for fetching movies by status");

		MovieResponse response = new MovieResponse();

		if (status == null) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<MovieResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Movie> movies = this.movieDao.findByStatus(status);

		if (CollectionUtils.isEmpty(movies)) {
			response.setResponseMessage("No movies found");
			response.setSuccess(false);

			return new ResponseEntity<MovieResponse>(response, HttpStatus.OK);
		}

		response.setMovies(movies);
		response.setResponseMessage("Movies fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<MovieResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<MovieResponse> fetchMoviesByTheatre(Integer theatreId) {

		LOG.info("Request received for fetching theatres by status");

		MovieResponse response = new MovieResponse();

		if (theatreId == null || theatreId == 0) {
			response.setResponseMessage("Missing status input!!!");
			response.setSuccess(false);

			return new ResponseEntity<MovieResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		Theatre theatre = this.theatreDao.findById(theatreId).orElse(null);
		
		if(theatre == null) {
			response.setResponseMessage("Theatre not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<MovieResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Movie> movies = this.movieDao.findByTheatreAndStatus(theatre, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(movies)) {
			response.setResponseMessage("No movies found");
			response.setSuccess(false);

			return new ResponseEntity<MovieResponse>(response, HttpStatus.OK);
		}

		response.setMovies(movies);
		response.setResponseMessage("Movies fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<MovieResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteMovie(int movieId) {

		LOG.info("Request received for updating the theatre");

		CommonApiResponse response = new CommonApiResponse();

		if (movieId == 0) {
			response.setResponseMessage("Missing input!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Movie movie = this.movieDao.findById(movieId).orElse(null);

		if (movie == null) {
			response.setResponseMessage("movie not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		movie.setStatus(ActiveStatus.DEACTIVATED.value());
		
		List<Shows> shows = this.showDao.findByStatusAndMovie(ShowStatus.ACTIVE.value(), movie);

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
		
		Movie savedMovie = this.movieDao.save(movie);

		if (savedMovie == null) {
			throw new MovieSaveFailedException("Failed to delete the Movie!!!");
		}

		response.setResponseMessage("Movie Deleted successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

}
