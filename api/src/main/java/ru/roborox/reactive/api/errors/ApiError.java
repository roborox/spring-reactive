package ru.roborox.reactive.api.errors;

public class ApiError {
	private String reason;

	public ApiError() {
	}

	public ApiError(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
