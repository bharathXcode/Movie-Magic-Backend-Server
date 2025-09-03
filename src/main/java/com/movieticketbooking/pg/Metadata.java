package com.movieticketbooking.pg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
	@JsonProperty("order_id")
	private String orderId;

	@JsonProperty("payment_id")
	private String paymentId;

}
