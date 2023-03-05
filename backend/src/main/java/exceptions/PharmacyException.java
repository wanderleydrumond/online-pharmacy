package exceptions;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response.Status;

import lombok.Getter;

@ApplicationException
public class PharmacyException extends RuntimeException {
	@Getter
	private Status httpStatus;
	@Getter
	private String header;
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;
	
	/* TODO constructor chaining
	public PharmacyException(Status httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}
*/
	
	public PharmacyException(Status httpStatus, String header, String message) {
		super(message);
		this.httpStatus = httpStatus;
		this.header = header;
	}
	
	// throw new PharmacyException(Response.Status.BAD_GATEWAY, "Erro XPTO", "Não foi possível fazer sei lá o quê para este id: " + id);
	
}