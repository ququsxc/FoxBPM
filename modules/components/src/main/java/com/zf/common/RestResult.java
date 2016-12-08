package com.zf.common;

public class RestResult {
	private int code;
	private String message;
	private Object data;

	public RestResult(int code) {
		this.code = code;
	}

	public RestResult(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public RestResult(int code, Object data) {
		this.code = code;
		this.data = data;
	}

	public RestResult(int code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static RestResult success() {
		return new RestResult(0);
	}

	public static RestResult success(Object data) {
		return new RestResult(0, data);
	}

	public static RestResult success(String message) {
		return new RestResult(0, message);
	}

	public static RestResult success(String message, Object data) {
		return new RestResult(0, message, data);
	}

	public static RestResult fail() {
		return new RestResult(1);
	}

	public static RestResult fail(String message) {
		return new RestResult(1, message);
	}

	public static RestResult fail(int code, String message) {
		return new RestResult(code, message);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
