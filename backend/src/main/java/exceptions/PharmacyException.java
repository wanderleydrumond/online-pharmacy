package exceptions;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.Getter;

/**
 * System custom exception that handles expected bad situations inside the whole code.
 * 
 * @author Wanderley Drumond
 */
@ApplicationException
public class PharmacyException extends RuntimeException {
	/**
	 * {@link Response} request status code
	 */
	@Getter
	private Status httpStatus;
	/**
	 * {@link Response} request header
	 */
	@Getter
	private String header;
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
	
	public PharmacyException(Status httpStatus, String header, String message) {
		super(message);
		this.httpStatus = httpStatus;
		this.header = header;
	}
}