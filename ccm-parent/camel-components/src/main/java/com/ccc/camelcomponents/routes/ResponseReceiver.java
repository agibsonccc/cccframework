package com.ccc.camelcomponents.routes;

public interface ResponseReceiver<T> {

	public Object setErrorResponse(Object response);
	
	public Object errorResponse();
	
	public T getSuccess();
	
	public void setAccess(T success);
	
}
