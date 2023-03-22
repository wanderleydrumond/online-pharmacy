package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Class that implements the proper behaviour every time that an expected exception like {@link PharmacyException} happens.
 * 
 * @author Wanderley Drumond
 */
@Provider
public class PharmacyExceptionHandler implements ExceptionMapper<PharmacyException> {

	@Override
	public Response toResponse(PharmacyException pharmacyException) {
		return Response.status(pharmacyException.getHttpStatus()).header(pharmacyException.getHeader(), pharmacyException.getMessage()).build();
	}

}