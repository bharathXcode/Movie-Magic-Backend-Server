package com.movieticketbooking.pg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorInfo {

	@JsonProperty("code")
	private String code;

	@JsonProperty("description")
	private String description;

	@JsonProperty("source")
	private String source;

	@JsonProperty("step")
	private String step;

	@JsonProperty("reason")
	private String reason;

	@JsonProperty("metadata")
	private Metadata metadata;
	
}
