package services;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import daos.ConfigurationDAO;
import daos.UserDAO;
import entities.Configuration;
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
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Object that contains all methods to manipulates database regarding users table.
	 */
	@Inject
	UserDAO userDAO;
	
	/**
	 * Object that contains all methods to manipulates database regarding configurations table.
	 */
	@Inject
	ConfigurationDAO configurationDAO;

	/**
	 * <ol>
	 * 	<li>Checks if the given user already exists in database</li>
	 * 	<li>Sets role attribute</li>
	 * 	<li>Sets isDeleted attribute</li>
	 * 	<li>Saves user into the database</li>
	 * </ol>
	 * 
	 * <em>{@link Role} is set again for safety reasons.</em>
	 * 
	 * @param user to be created with name, username, password and role filled
	 * @return
	 * 		<ul>
	 * 			<li>The new user id, if persisted in database</li>
	 * 			<li>A {@link PharmacyException} with response code 409 (CONFLICT) if already exists a user with the same credentials.</li>
	 * 			<li>A {@link PharmacyException} with response code 400 (BAD REQUEST) if the request was made with username and password set as null.</li>
	 * 		</ul>
	 */
	public int signUp(User user) {
		Optional<User> userToFind = userDAO.signIn(user.getUsername(), user.getPassword());
		
		if (userToFind.isPresent()) {
			throw new PharmacyException(Response.Status.CONFLICT, "User already exists", "Please, proceed with sign in instead");
		}
		
		user.setRole(Role.VISITOR);
		user.setIsDeleted(false);
		
		userDAO.persist(user);
		
		if (user.getId() == null) {
			throw new PharmacyException(Response.Status.BAD_REQUEST, "Error creating user", "User not created");
		}
		
		return user.getId();
	}

	
	/**
	 * <ol>
	 * 	<li>Search for the user in database.</li>
	 * 	<li>Sets a random UUID for this user.</li>
	 * 	<li>Saves the UUID in the user table.</li>
	 * 	<li>Updates the amount of system sign ins in the configurations table</li>
	 * </ol>
	 * 
	 * @param username of the user to be signed in
	 * @param password of the user to be signed in
	 * @return
	 * 		<ul>
	 * 			<li>The {@link User} object if the user was signed in</li>
	 * 			<li>A {@link PharmacyException} with response code 401 (UNAUTHORIZED) if username and/or password are not found in database.</li>
	 * 		</ul>
	 */
	public User signIn(String username, String password) {
		Optional<User> user = userDAO.signIn(username, password);
		
		if (user.isEmpty()) {
			throw new PharmacyException(Response.Status.UNAUTHORIZED, "User not found", "The given credentials are invalid");
		}
		
		user.get().setToken(UUID.randomUUID());
		userDAO.merge(user.get());
		
		updateTotalSignIns();
		
		return user.get();
	}
	
	/**
	 * Gets the {@link User} that owns the given token.
	 * 
	 * @param token user identifier key
	 * @return
	 * 		<ul>
	 * 			<li>The {@link User} that owns the given token </li>
	 * 			<li>A {@link PharmacyException} with HTTP {@link Response} status 404 (NOT FOUND) if the given token does not exists in database</li>
	 * 		</ul>
	 */
	public User getByToken(UUID token) {
		Optional<User> userToFind = userDAO.findByUUID(token);
		
		if (userToFind.isEmpty()) {
			throw new PharmacyException(Response.Status.NOT_FOUND, "User not found", "The given token does not exists in database");
		}
		
		return userToFind.get();
	}
	
	/**
	 * <ul>
	 * 	<li>If the system already has other sign ins, increments the amount and save it in database.</li>
	 * 	<li>If it is the very first sign in the system, creates the row in the configurations table and sets as "1" as value.</li>
	 * </ul>
	 */
	public void updateTotalSignIns() {
		String keyword = "total of sign ins";
		Optional<Configuration> configurationOptional = configurationDAO.findValueByKeyWord(keyword);
		
		configurationOptional.ifPresentOrElse(congigurationElement -> {
			int amountOfSignIns = Integer.valueOf(configurationOptional.get().getValue());
			configurationOptional.get().setValue(String.valueOf(++amountOfSignIns));
			configurationDAO.merge(configurationOptional.get());
			
		}, () -> configurationDAO.persist(new Configuration(keyword, "1")));
	}


	/**
	 * Signs out a user from the system.
	 * 
	 * @param token logged user identifier key
	 * @return true if the user was successfully signed out from the system
	 * @throws {@link PharmacyException} with 400 status code (BAD REQUEST) if:
	 * 		<ul>
	 * 			<li>The user that owns the given token is already signed out of the system</li>
	 * 			<li>The given token is null</li>
	 * 		</ul>
	 */
	public Boolean signOut(UUID token) {
		Integer amountOfRowsUpdated = userDAO.signOut(token);
		System.out.println("amountOfRowsUpdated: " + amountOfRowsUpdated);
		
		switch (amountOfRowsUpdated) {
		case 0:
			throw new PharmacyException(Response.Status.BAD_REQUEST, "Error", "Sign out not fulfilled");

		case 1:
			return true;
			
		default:
			throw new PharmacyException(Response.Status.BAD_REQUEST, "Error", "Token cannot be null");
		}
	}

	/**
	 * <ol>
	 * 	<li>Checks if the logged user has the ADMNINSTRATOR role</li>
	 * 	<li>Approves the user</li>
	 * </ol>
	 * 
	 * @param token			  logged user identifier key
	 * @param userToApproveId id of the user to be approved
	 * @return
	 * 		<ul>
	 * 			<li>True, if 1 row was affected in database</li>
	 * 			<li>False, if 0 rows was affected in database (never happens)</li>
	 * 		</ul>
	 * @throws {@link PharmacyException} with status code:
	 * 		<ul>
	 * 			<li><strong>403 (FORBIDDEN)</strong> if the logged user has not the ADMINISTRATOR role</li>
	 * 			<li><strong>400 (BAD REQUEST)</strong>if the amount of rows updated is 0</li>
	 * 		</ul>
	 */
	public Boolean approve(UUID token, Integer userToApproveId) {
		Boolean isAdmin = verifyIfIsAdmin(token);
		
		if (Boolean.FALSE.equals(isAdmin)) {
			throw new PharmacyException(Response.Status.FORBIDDEN, "insufficient privileges", "Only administrators can execute this action");
		}
		
		Integer updatedRowsInUsersTable = userDAO.approve(userToApproveId);
		
		if (updatedRowsInUsersTable == 0) {
			throw new PharmacyException(Response.Status.BAD_REQUEST, "Request not answered", "The requested row wasn't updated");
		}
		
		return updatedRowsInUsersTable == 1 ? true : false;
	}


	private Boolean verifyIfIsAdmin(UUID token) {
		Boolean isAdmin = userDAO.checkIfIsAdmin(token);
		
		if (isAdmin == null) {
			throw new PharmacyException(Response.Status.SERVICE_UNAVAILABLE, "Error", "Error in database occured");
		}
		
		return isAdmin;
	}
}