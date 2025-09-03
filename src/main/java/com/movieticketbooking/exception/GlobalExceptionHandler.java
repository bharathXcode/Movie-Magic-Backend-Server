package com.movieticketbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.movieticketbooking.dto.CommonApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<CommonApiResponse> handleUserNotFoundException(UserNotFoundException ex) {
		String responseMessage = ex.getMessage();

		CommonApiResponse apiResponse = CommonApiResponse.builder().responseMessage(responseMessage).isSuccess(false)
				.build();
		return new ResponseEntity<CommonApiResponse>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(UserSaveFailedException.class)
	public ResponseEntity<CommonApiResponse> handleUserRegistrationFailedException(UserSaveFailedException ex) {
		String responseMessage = ex.getMessage();

		CommonApiResponse apiResponse = CommonApiResponse.builder().responseMessage(responseMessage).isSuccess(false)
				.build();
		return new ResponseEntity<CommonApiResponse>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(LocationSaveFailedException.class)
	public ResponseEntity<CommonApiResponse> handleLocationSaveFailedException(LocationSaveFailedException ex) {
		String responseMessage = ex.getMessage();

		CommonApiResponse apiResponse = CommonApiResponse.builder().responseMessage(responseMessage).isSuccess(false)
				.build();
		return new ResponseEntity<CommonApiResponse>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(MovieSaveFailedException.class)
	public ResponseEntity<CommonApiResponse> handleMovieSaveFailedException(MovieSaveFailedException ex) {
		String responseMessage = ex.getMessage();

		CommonApiResponse apiResponse = CommonApiResponse.builder().responseMessage(responseMessage).isSuccess(false)
				.build();
		return new ResponseEntity<CommonApiResponse>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(TheatreSaveFailedException.class)
	public ResponseEntity<CommonApiResponse> handleTheatreSaveFailedException(TheatreSaveFailedException ex) {
		String responseMessage = ex.getMessage();

		CommonApiResponse apiResponse = CommonApiResponse.builder().responseMessage(responseMessage).isSuccess(false)
				.build();
		return new ResponseEntity<CommonApiResponse>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(ScreenSaveFailedException.class)
	public ResponseEntity<CommonApiResponse> handleScreenSaveFailedException(ScreenSaveFailedException ex) {
		String responseMessage = ex.getMessage();

		CommonApiResponse apiResponse = CommonApiResponse.builder().responseMessage(responseMessage).isSuccess(false)
				.build();
		return new ResponseEntity<CommonApiResponse>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(ShowSaveFailedException.class)
	public ResponseEntity<CommonApiResponse> handleShowSaveFailedException(ShowSaveFailedException ex) {
		String responseMessage = ex.getMessage();

		CommonApiResponse apiResponse = CommonApiResponse.builder().responseMessage(responseMessage).isSuccess(false)
				.build();
		return new ResponseEntity<CommonApiResponse>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(BookingNotFoundException.class)
	public ResponseEntity<CommonApiResponse> handleBookingNotFoundException(BookingNotFoundException ex) {
		String responseMessage = ex.getMessage();

		CommonApiResponse apiResponse = CommonApiResponse.builder().responseMessage(responseMessage).isSuccess(false)
				.build();
		return new ResponseEntity<CommonApiResponse>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(ReviewSaveFailedException.class)
	public ResponseEntity<CommonApiResponse> handleReviewSaveFailedException(ReviewSaveFailedException ex) {
		String responseMessage = ex.getMessage();

		CommonApiResponse apiResponse = CommonApiResponse.builder().responseMessage(responseMessage).isSuccess(false)
				.build();
		return new ResponseEntity<CommonApiResponse>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
