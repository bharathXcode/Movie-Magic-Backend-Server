package com.movieticketbooking.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.RegisterUserRequestDto;
import com.movieticketbooking.dto.UserLoginRequest;
import com.movieticketbooking.dto.UserLoginResponse;
import com.movieticketbooking.dto.UserResponseDto;
import com.movieticketbooking.dto.UserWalletUpdateResponse;
import com.movieticketbooking.entity.User;
import com.movieticketbooking.pg.RazorPayPaymentResponse;
import com.movieticketbooking.service.UserService;
import com.razorpay.RazorpayException;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

	@Autowired
	private UserService userService;

	// RegisterUserRequestDto, we will set only email, password & role from UI
	@PostMapping("/admin/register")
	@Operation(summary = "Api to register Admin")
	public ResponseEntity<CommonApiResponse> registerAdmin(@RequestBody RegisterUserRequestDto request) {
		return userService.registerAdmin(request);
	}

	// for customer and theatre
	@PostMapping("register")
	@Operation(summary = "Api to register Theatre & Customer")
	public ResponseEntity<CommonApiResponse> registerUser(@RequestBody RegisterUserRequestDto request) {
		return this.userService.registerUser(request);
	}

	@PostMapping("login")
	@Operation(summary = "Api to login any User")
	public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
		return userService.login(userLoginRequest);
	}

	@GetMapping("/fetch/role-wise")
	@Operation(summary = "Api to get Users By Role")
	public ResponseEntity<UserResponseDto> fetchAllUsersByRole(@RequestParam("role") String role)
			throws JsonProcessingException {
		return userService.getUsersByRole(role);
	}

	@GetMapping("/fetch/user-id")
	@Operation(summary = "Api to get User Detail By User Id")
	public ResponseEntity<UserResponseDto> fetchUserById(@RequestParam("userId") int userId) {
		return userService.getUserById(userId);
	}

	@GetMapping("/update/status")
	@Operation(summary = "Api to update the user status")
	public ResponseEntity<CommonApiResponse> updateUserStatus(@RequestParam("userId") int userId,
			@RequestParam("status") String status) throws JsonProcessingException {
		return userService.updateUserStatus(userId, status);
	}

// without razorpay payment gateway integration
	
//	@PostMapping("/add/wallet/money")
//	@Operation(summary = "Api to add wallet money")
//	public ResponseEntity<CommonApiResponse> addMoneyInWallet(@RequestBody AddWalletMoneyRequestDto request) {
//		return userService.addMoneyInWallet(request);
//	}
	
	// for Theatre wallet fetch
	@GetMapping("/wallet/fetch")
	@Operation(summary = "Api to fetch user wallet money")
	public ResponseEntity<BigDecimal> getCustomerWallet(@RequestParam("userId") int userId) {
		return userService.fetchCustomerWalletDetail(userId);
	}
	
	
	// below 2 apis are for wallet update with razorpay payment gateway integrtion
	@PutMapping("update/wallet")
	@Operation(summary = "Api to create the razor pay order")
	public ResponseEntity<UserWalletUpdateResponse> createRazorPayOrder(@RequestBody User user)
			throws RazorpayException {
		return userService.createRazorPayOrder(user);
	}

	@PutMapping("razorpPay/response")
	@Operation(summary = "Api to update the user wallet based on razorpay response")
	public ResponseEntity<CommonApiResponse> updateUserWallet(@RequestBody RazorPayPaymentResponse razorPayResponse)
			throws RazorpayException {
		return userService.handleRazorPayPaymentResponse(razorPayResponse);
	}

}
