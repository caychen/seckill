package org.seckill.dto;

public class SeckillResult<T> {

	private boolean isOk;

	private T data;

	private String error;

	public SeckillResult(boolean isOk, T data) {
		super();
		this.isOk = isOk;
		this.data = data;
	}

	public SeckillResult(boolean isOk, String error) {
		super();
		this.isOk = isOk;
		this.error = error;
	}

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
