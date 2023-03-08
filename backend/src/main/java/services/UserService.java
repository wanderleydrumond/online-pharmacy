package services;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import daos.UserDAO;
import entities.User;
import enums.Role;
import exceptions.PharmacyException;

/**
 * Class that contains all the programmatic logic regarding the user.
 * 
 * @author Wanderley Drumond
 */
@RequestScoped
public class UserService implements Serializable {
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	UserDAO userDAO;

	public int signUp(User user) {
		user.setRole(Role.VISITOR);
		user.setIsDeleted(false);
		
		userDAO.persist(user);
		
		if (user.getId() == null) {
			throw new PharmacyException(Response.Status.BAD_REQUEST, "Error creating user", "User not created");
		}
		
		return user.getId();
	}
}