package org.coursera.androidcapstone.common.oauth;

/**
 * A special class made to specify exceptions that are thrown by our
 * SecuredRestBuilder.
 * 
 * A more robust implementation would probably have fields for tracking
 * the type of exception (e.g., bad password, etc.).
 * 
 * @author jules
 *
 */
public class SecuredRestException extends RuntimeException {
	private static final long serialVersionUID = -3872838278232618576L;

	public SecuredRestException() {
		super();
	}

	public SecuredRestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SecuredRestException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecuredRestException(String message) {
		super(message);
	}

	public SecuredRestException(Throwable cause) {
		super(cause);
	}
}
