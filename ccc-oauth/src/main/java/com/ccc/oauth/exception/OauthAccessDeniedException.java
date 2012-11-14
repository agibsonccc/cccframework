package com.ccc.oauth.exception;

public class OauthAccessDeniedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7196981239553520571L;

	public OauthAccessDeniedException() {
		super();
	}

	public OauthAccessDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public OauthAccessDeniedException(String message) {
		super(message);
	}

	public OauthAccessDeniedException(Throwable cause) {
		super(cause);
	}

}
