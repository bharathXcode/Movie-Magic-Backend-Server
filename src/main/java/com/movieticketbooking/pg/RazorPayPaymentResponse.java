package com.movieticketbooking.pg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RazorPayPaymentResponse {

	@JsonProperty("razorpay_payment_id")
	private String razorpayPaymentId;

	@JsonProperty("razorpay_order_id")
	private String razorpayOrderId;

	@JsonProperty("razorpay_signature")
	private String razorpaySignature;

	@JsonProperty("error")
	private ErrorInfo error;

}
