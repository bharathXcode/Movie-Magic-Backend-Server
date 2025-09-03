package com.movieticketbooking.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticketbooking.dao.PgTransactionDao;
import com.movieticketbooking.dao.TheatreDao;
import com.movieticketbooking.dao.UserDao;
import com.movieticketbooking.dto.AddWalletMoneyRequestDto;
import com.movieticketbooking.dto.CommonApiResponse;
import com.movieticketbooking.dto.RegisterUserRequestDto;
import com.movieticketbooking.dto.UserDto;
import com.movieticketbooking.dto.UserLoginRequest;
import com.movieticketbooking.dto.UserLoginResponse;
import com.movieticketbooking.dto.UserResponseDto;
import com.movieticketbooking.dto.UserWalletUpdateResponse;
import com.movieticketbooking.entity.PgTransaction;
import com.movieticketbooking.entity.Theatre;
import com.movieticketbooking.entity.User;
import com.movieticketbooking.exception.UserSaveFailedException;
import com.movieticketbooking.pg.Notes;
import com.movieticketbooking.pg.Prefill;
import com.movieticketbooking.pg.RazorPayPaymentRequest;
import com.movieticketbooking.pg.RazorPayPaymentResponse;
import com.movieticketbooking.pg.Theme;
import com.movieticketbooking.utility.Constants.ActiveStatus;
import com.movieticketbooking.utility.Constants.PaymentGatewayTxnStatus;
import com.movieticketbooking.utility.Constants.PaymentGatewayTxnType;
import com.movieticketbooking.utility.Constants.UserRole;
import com.movieticketbooking.utility.JwtUtils;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class UserService {

	private final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TheatreDao theatreDao;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PgTransactionDao pgTransactionDao;

	@Value("${com.movieticketbooking.paymentGateway.razorpay.key}")
	private String razorPayKey;

	@Value("${com.movieticketbooking.paymentGateway.razorpay.secret}")
	private String razorPaySecret;


	public ResponseEntity<CommonApiResponse> registerAdmin(RegisterUserRequestDto registerRequest) {

		LOG.info("Request received for Register Admin");

		CommonApiResponse response = new CommonApiResponse();

		if (registerRequest == null) {
			response.setResponseMessage("user is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (registerRequest.getEmailId() == null || registerRequest.getPassword() == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User existingUser = this.userDao.findByEmailIdAndStatus(registerRequest.getEmailId(),
				ActiveStatus.ACTIVE.value());

		if (existingUser != null) {
			response.setResponseMessage("User already register with this Email");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = RegisterUserRequestDto.toUserEntity(registerRequest);

		user.setRole(UserRole.ROLE_ADMIN.value());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setStatus(ActiveStatus.ACTIVE.value());

		existingUser = this.userDao.save(user);

		if (existingUser == null) {
			response.setResponseMessage("failed to register admin");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		response.setResponseMessage("Admin registered Successfully");
		response.setSuccess(true);

		LOG.info("Response Sent!!!");

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> registerUser(RegisterUserRequestDto request) {

		LOG.info("Received request for register user");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("user is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User existingUser = this.userDao.findByEmailIdAndStatus(request.getEmailId(), ActiveStatus.ACTIVE.value());

		if (existingUser != null) {
			response.setResponseMessage("User with this Email Id already resgistered!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getRole() == null) {
			response.setResponseMessage("bad request ,Role is missing");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = RegisterUserRequestDto.toUserEntity(request);

		// in case of HR and Employee company id should be present to know from which
		// company they are

//		if (request.getRole().equals(UserRole.ROLE_COMPANY_HR.value())
//				|| request.getRole().equals(UserRole.ROLE_COMPANY_EMPLOYEE.value())) {
//
//			if (request.getCompanyId() == 0) {
//				response.setResponseMessage("bad request ,company is missing");
//				response.setSuccess(false);
//
//				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
//			}
//
//			Company company = this.companyDao.findById(request.getCompanyId()).orElse(null);
//
//			if (company == null) {
//				response.setResponseMessage("company not found!!!");
//				response.setSuccess(false);
//
//				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
//			}
//
//			if (!company.getStatus().equals(CompanyStatus.ACTIVE.value())) {
//				response.setResponseMessage("Company Approval Pending, Contact Admin!!!");
//				response.setSuccess(false);
//
//				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
//			}
//
//			user.setCompany(company);
//
//		}

		String encodedPassword = passwordEncoder.encode(user.getPassword());

		user.setWalletAmount(BigDecimal.ZERO);
		user.setStatus(ActiveStatus.ACTIVE.value());
		user.setPassword(encodedPassword);

		existingUser = this.userDao.save(user);

		if (existingUser == null) {
			throw new UserSaveFailedException("Registration Failed because of Technical issue:(");
		}

		response.setResponseMessage("User registered Successfully");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserLoginResponse> login(UserLoginRequest loginRequest) {

		LOG.info("Received request for User Login");

		UserLoginResponse response = new UserLoginResponse();

		if (loginRequest == null) {
			response.setResponseMessage("Missing Input");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String jwtToken = null;
		User user = null;

		user = this.userDao.findByEmailId(loginRequest.getEmailId());

		if (user == null) {
			response.setResponseMessage("User with this Email Id not registered in System!!!");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(user.getRole()));

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmailId(),
					loginRequest.getPassword(), authorities));
		} catch (Exception ex) {
			response.setResponseMessage("Invalid email or password.");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		jwtToken = jwtUtils.generateToken(loginRequest.getEmailId());

		if (!user.getStatus().equals(ActiveStatus.ACTIVE.value())) {
			response.setResponseMessage("User is not active");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		UserDto userDto = UserDto.toUserDtoEntity(user);

		// user is authenticated
		if (jwtToken != null) {
			response.setUser(userDto);
			response.setResponseMessage("Logged in sucessful");
			response.setSuccess(true);
			response.setJwtToken(jwtToken);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.OK);
		}

		else {
			response.setResponseMessage("Failed to login");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

	}

	public ResponseEntity<UserResponseDto> getUsersByRole(String role) {

		UserResponseDto response = new UserResponseDto();

		if (role == null) {
			response.setResponseMessage("missing role");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();

		users = this.userDao.findByRoleAndStatus(role, ActiveStatus.ACTIVE.value());

		if (users.isEmpty()) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
		}

		List<UserDto> userDtos = new ArrayList<>();

		for (User user : users) {

			UserDto dto = UserDto.toUserDtoEntity(user);
			userDtos.add(dto);

		}

		response.setUsers(userDtos);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserResponseDto> getUserById(int userId) {

		UserResponseDto response = new UserResponseDto();

		if (userId == 0) {
			response.setResponseMessage("Invalid Input");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();

		User user = this.userDao.findById(userId).orElse(null);

		if (user == null) {
			response.setResponseMessage("User not found!!!");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		users.add(user);

		if (users.isEmpty()) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
		}

		List<UserDto> userDtos = new ArrayList<>();

		for (User u : users) {

			UserDto dto = UserDto.toUserDtoEntity(u);

			userDtos.add(dto);

		}

		response.setUsers(userDtos);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateUserStatus(int userId, String status) {

		CommonApiResponse response = new CommonApiResponse();

		if (userId == 0 || status == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userDao.findById(userId).orElse(null);

		if (user == null) {
			response.setResponseMessage("user not found!!!");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		user.setStatus(status);
		userDao.save(user);

		if (status.equals(ActiveStatus.DEACTIVATED.value()) && user.getRole().equals(UserRole.ROLE_THEATRE.value())) {
			Theatre theatre = user.getTheatre();

			if (theatre != null) {
				theatre.setStatus(ActiveStatus.DEACTIVATED.value());
				theatreDao.save(theatre);
			}

		} else if (status.equals(ActiveStatus.ACTIVE.value()) && user.getRole().equals(UserRole.ROLE_THEATRE.value())) {
			Theatre theatre = user.getTheatre();

			if (theatre != null) {
				theatre.setStatus(ActiveStatus.ACTIVE.value());
				theatreDao.save(theatre);
			}

		}

		response.setResponseMessage("User Status updated successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> addMoneyInWallet(AddWalletMoneyRequestDto request) {
		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("Bad Request, improper request data");
			response.setSuccess(true);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getUserId() == 0) {
			response.setResponseMessage("Bad Request, user id is missing");
			response.setSuccess(true);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getWalletAmount() == null || request.getWalletAmount().compareTo(BigDecimal.ZERO) < 0) {
			response.setResponseMessage("Bad Request, Enter valid amount!!!");
			response.setSuccess(true);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = userDao.findById(request.getUserId()).orElse(null);

		if (user == null) {
			response.setResponseMessage("Bad Request, user not found!!!");
			response.setSuccess(true);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		BigDecimal walletAmount = user.getWalletAmount();
		BigDecimal walletToUpdate = walletAmount.add(request.getWalletAmount());

		user.setWalletAmount(walletToUpdate);

		User udpatedUser = userDao.save(user);

		if (udpatedUser != null) {
			response.setResponseMessage("Money added in wallet successfully!!!");
			response.setSuccess(true);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
		} else {
			response.setResponseMessage("Failed to add the money in wallet!!!");
			response.setSuccess(true);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<BigDecimal> fetchCustomerWalletDetail(int userId) {
		User user = userDao.findById(userId).orElse(null);

		return new ResponseEntity<>(user.getWalletAmount(), HttpStatus.OK);

	}

	public ResponseEntity<UserWalletUpdateResponse> createRazorPayOrder(User request) throws RazorpayException {

		UserWalletUpdateResponse response = new UserWalletUpdateResponse();

		if (request == null) {
			response.setResponseMessage("Invalid Input");
			response.setSuccess(false);
			return new ResponseEntity<UserWalletUpdateResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getId() == 0 || request.getWalletAmount() == null
				|| request.getWalletAmount().compareTo(BigDecimal.ZERO) <= 0) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
			return new ResponseEntity<UserWalletUpdateResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User customer = this.userDao.findById(request.getId()).orElse(request);

		if (customer == null || !customer.getRole().equals(UserRole.ROLE_CUSTOMER.value())) {
			response.setResponseMessage("Customer Not Found");
			response.setSuccess(false);
			return new ResponseEntity<UserWalletUpdateResponse>(response, HttpStatus.BAD_REQUEST);
		}

		BigDecimal existingWalletAmount = customer.getWalletAmount();

		// write payment gateway code here

		// key : rzp_test_9C5DF9gbJINYTA
		// secret: WYqJeY6CJD1iw7cDZFv1eWl0

		String receiptId = generateUniqueRefId();

		RazorpayClient razorpay = new RazorpayClient(razorPayKey, razorPaySecret);

		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", convertRupeesToPaisa(request.getWalletAmount()));
		orderRequest.put("currency", "INR");
		orderRequest.put("receipt", receiptId);
		JSONObject notes = new JSONObject();
		notes.put("note", "Credit in Wallet - Movie Magic");
		orderRequest.put("notes", notes);

		Order order = razorpay.orders.create(orderRequest);

		if (order == null) {
			LOG.error("Null Response from RazorPay for creation of");
			response.setResponseMessage("Failed to update the Wallet");
			response.setSuccess(false);
			return new ResponseEntity<UserWalletUpdateResponse>(response, HttpStatus.BAD_REQUEST);
		}

		LOG.info(order.toString()); // printing the response which we got from RazorPay

		String orderId = order.get("id");

		PgTransaction createOrder = new PgTransaction();
		createOrder.setAmount(request.getWalletAmount());
		createOrder.setReceiptId(receiptId);
		createOrder.setRequestTime(receiptId);
		createOrder.setType(PaymentGatewayTxnType.CREATE_ORDER.value());
		createOrder.setCustomer(customer);
		createOrder.setOrderId(orderId); // fetching order id which is created at Razor Pay which we got in response

		if (order.get("status").equals("created")) {
			createOrder.setStatus(PaymentGatewayTxnStatus.SUCCESS.value());
		} else {
			createOrder.setStatus(PaymentGatewayTxnStatus.FAILED.value());
		}

		PgTransaction saveCreateOrderTxn = this.pgTransactionDao.save(createOrder);

		if (saveCreateOrderTxn == null) {
			LOG.error("Failed to save Payment Gateway CReate Order entry in DB");
		}

		PgTransaction payment = new PgTransaction();
		payment.setAmount(request.getWalletAmount());
		payment.setReceiptId(receiptId);
		payment.setRequestTime(receiptId);
		payment.setType(PaymentGatewayTxnType.PAYMENT.value());
		payment.setCustomer(customer);
		payment.setOrderId(orderId); // fetching order id which is created at Razor Pay which we got in response
		payment.setStatus(PaymentGatewayTxnStatus.FAILED.value());
		// from callback api we will actual response from RazorPay, initially keeping it
		// FAILED, once get success response from PG,
		// we will update it

		PgTransaction savePaymentTxn = this.pgTransactionDao.save(payment);

		if (savePaymentTxn == null) {
			LOG.error("Failed to save Payment Gateway Payment entry in DB");
		}

		// Creating RazorPayPaymentRequest to send to Frontend

		RazorPayPaymentRequest razorPayPaymentRequest = new RazorPayPaymentRequest();
		razorPayPaymentRequest.setAmount(convertRupeesToPaisa(request.getWalletAmount()));
		// razorPayPaymentRequest.setCallbackUrl("http://localhost:8080/pg/razorPay/callBack/response");
		razorPayPaymentRequest.setCurrency("INR");
		razorPayPaymentRequest.setDescription("Credit in Wallet - Movie Magic");
		razorPayPaymentRequest
				.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSkk6YF76xVBZx9H-lqSouQPaZCIfmxyYnymtlGIZ7EIQRPX4I32LhdR-zyKSevDlpG9E4&usqp=CAU");
		razorPayPaymentRequest.setKey(razorPayKey);
		razorPayPaymentRequest.setName("Movie Magic");

		Notes note = new Notes();
		note.setAddress("Dummy Address");

		razorPayPaymentRequest.setNotes(note);
		razorPayPaymentRequest.setOrderId(orderId);

		Prefill prefill = new Prefill();
		prefill.setContact(customer.getPhoneNo());
		prefill.setEmail(customer.getEmailId());
		prefill.setName(customer.getFirstName() + " " + customer.getLastName());

		razorPayPaymentRequest.setPrefill(prefill);

		Theme theme = new Theme();
		theme.setColor("#eb455f");

		razorPayPaymentRequest.setTheme(theme);

		try {
			String jsonRequest = objectMapper.writeValueAsString(razorPayPaymentRequest);
			System.out.println("*****************");
			System.out.println(jsonRequest);
			System.out.println("*****************");
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		customer.setWalletAmount(existingWalletAmount.add(request.getWalletAmount()));
//
//		User updatedCustomer = this.userService.updateUser(customer);
//
//		if (updatedCustomer == null) {
//			response.setResponseMessage("Failed to update the Wallet");
//			response.setSuccess(false);
//			return new ResponseEntity<UserWalletUpdateResponse>(response, HttpStatus.BAD_REQUEST);
//		}

		response.setRazorPayRequest(razorPayPaymentRequest);
		response.setResponseMessage("Payment Order Created Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<UserWalletUpdateResponse>(response, HttpStatus.OK);
	}
	
	private int convertRupeesToPaisa(BigDecimal rupees) {
		// Multiply the rupees by 100 to get the equivalent in paisa
		BigDecimal paisa = rupees.multiply(new BigDecimal(100));
		return paisa.intValue();
	}

	// for razor pay receipt id
	private String generateUniqueRefId() {
		// Get current timestamp in milliseconds
		long currentTimeMillis = System.currentTimeMillis();

		// Generate a 6-digit UUID (random number)
		String randomDigits = UUID.randomUUID().toString().substring(0, 6);

		// Concatenate timestamp and random digits
		String uniqueRefId = currentTimeMillis + "-" + randomDigits;

		return uniqueRefId;
	}

	public ResponseEntity<CommonApiResponse> handleRazorPayPaymentResponse(RazorPayPaymentResponse razorPayResponse) {

		LOG.info("razor pay response came from frontend");

		CommonApiResponse response = new CommonApiResponse();

		if (razorPayResponse == null || razorPayResponse.getRazorpayOrderId() == null) {
			response.setResponseMessage("Invalid Input response");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		PgTransaction paymentTransaction = this.pgTransactionDao
				.findByTypeAndOrderId(PaymentGatewayTxnType.PAYMENT.value(), razorPayResponse.getRazorpayOrderId());

		User customer = paymentTransaction.getCustomer();
		BigDecimal existingBalance = customer.getWalletAmount();

		BigDecimal walletBalanceToAdd = paymentTransaction.getAmount();

		String razorPayRawResponse = "";
		try {
			razorPayRawResponse = objectMapper.writeValueAsString(razorPayResponse);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		paymentTransaction.setRawResponse(razorPayRawResponse);

		if (razorPayResponse.getError() == null) {
			paymentTransaction.setStatus(PaymentGatewayTxnStatus.SUCCESS.value());

			customer.setWalletAmount(existingBalance.add(walletBalanceToAdd));

			User updatedCustomer = this.userDao.save(customer);

			if (updatedCustomer == null) {
				LOG.error("Failed to update the wallet for order id: " + razorPayResponse.getRazorpayOrderId());
			} else {
				LOG.info("Wallet Updated Successful");
			}

		} else {
			paymentTransaction.setStatus(PaymentGatewayTxnStatus.FAILED.value());
		}

		PgTransaction updatedTransaction = this.pgTransactionDao.save(paymentTransaction);

		if (updatedTransaction.getStatus().equals(PaymentGatewayTxnStatus.FAILED.value())) {
			response.setResponseMessage("Failed to update the User Wallet");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
		} else {
			response.setResponseMessage("User Wallet Updated Successful");
			response.setSuccess(true);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
		}

	}

}
