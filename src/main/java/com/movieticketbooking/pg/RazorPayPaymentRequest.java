package com.movieticketbooking.pg;

import lombok.Data;

@Data
public class RazorPayPaymentRequest {

	private String key;
	private int amount;
	private String currency;
	private String name;
	private String description;
	private String image;
	private String orderId;
	private Prefill prefill;
	private Notes notes;
	private Theme theme;

}
