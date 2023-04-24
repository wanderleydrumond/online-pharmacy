package services;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import daos.ConfigurationDAO;
import daos.UserDAO;
import dtos.DashboardDTO;
import dtos.UserDTO;
import entities.Configuration;
import entities.Order;
import entities.User;
import enums.Role;
import exceptions.PharmacyException;
import mappers.UserMapper;

/**
 * Class that contains all the programmatic logic regarding the user.
 * 
 * @author Wanderley Drumond
 */
@RequestScoped
public class UserService implements Serializable {
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Object that contains all methods to manipulates database regarding users table.
	 */
	@Inject
	private UserDAO userDAO;
	
	/**
	 * Object that contains all methods to manipulates database regarding configurations table.
	 */
	@Inject
	private ConfigurationDAO configurationDAO;
	
	/**
	 * Object that contains all order service methods.
	 */
	@Inject
	private OrderService orderService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private UserMapper userMapper;

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
	 * 	<li>Updates the amount of system sign ins in the configurations table for clients</li>
	 *  <li>Checks if the logged user has the client role</li>
	 *  <li>Gets the non concluded order from the logged user</li>
	 *  <li>
	 *  	Checks if this there is a cart
	 *  	<ol>
	 *  		<li>Gets the time of two days ago</li>
	 *  		<ol>
	 *  			<li>Checks if the order last update is equal to two days ago</li>
	 *  				<ol>
	 *  					<li>Deletes the cart</li>
	 *  				</ol> 
	 *  		</ol>
	 *  	</ol>
	 *  </li>
	 * </ol>
	 * 
	 * @param username of the user to sign in
	 * @param password of the user to sign in
	 * @return The {@link User} object
	 * @throws {@link PharmacyException} with response code 401 (UNAUTHORIZED) if username and/or password are not found in database.
	 * @throws {@link PharmacyException} with response code 403 (FORBIDDEN) if user role is VISITOR
	 */
	public User signIn(String username, String password) {
		Optional<User> optionalUser = userDAO.signIn(username, password);
		
		if (optionalUser.isEmpty()) {
			throw new PharmacyException(Response.Status.UNAUTHORIZED, "User not found", "The given credentials are invalid");
		}
		
		User user = optionalUser.get();
		
		if (user.getRole().equals(Role.VISITOR)) {
			throw new PharmacyException(Response.Status.FORBIDDEN, "A visitor cannot sign in the system", "Wait until the administrator approves your account");
		}
		
		user.setToken(UUID.randomUUID());
		userDAO.merge(user);
		
		if (user.getRole().equals(Role.CLIENT)) {
			updateTotalSignIns();
		}
		
		Order order = orderService.getNonConcluded(user.getToken());
		
		if (order != null) {
			Timestamp twoDaysAgo = Timestamp.valueOf(LocalDateTime.now().minusDays(2L));
			if (order.getLastUpdate().before(twoDaysAgo)) {
				orderService.emptyCart(user.getToken(), order.getId());
			}
		}
		return user;
	}
	
	/**
	 * Gets the {@link User} that owns the given token.
	 * 
	 * @param token user identifier key
	 * @return The {@link User} that owns the given token
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 404 (NOT FOUND) if the given token does not exists in database
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
			short amountOfSignIns = Short.valueOf(configurationOptional.get().getValue());
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


	/**
	 * <p>Verify if the logged user is an administrator.</p>
	 * <p><em>Auxiliary method.</em></p>
	 * 
	 * @param token logged user identifier key
	 * @return 
	 * 		<ul>
	 * 			<li>True, if the user is administrator</li>
	 * 			<li>False, if the user is not an administrator</li>
	 * 		</ul>
	 */
	public Boolean verifyIfIsAdmin(UUID token) {
		Boolean isAdmin = userDAO.checkIfIsAdmin(token);
		
		if (isAdmin == null) {
			throw new PharmacyException(Response.Status.SERVICE_UNAVAILABLE, "Error", "Error in database occured");
		}
		
		return isAdmin;
	}

	/**
	 * <ol>
	 * 	<li>Gets the user to be updated.</li>
	 * 	<li>fills the informations that cannot be updated:</li>
	 * 		<ul>
	 * 			<li>role</li>
	 * 			<li>username</li>
	 * 			<li>token</li>
	 * 		</ul>
	 * 	<li>Checks if the user is deleted</li>
	 * 	<li>Checks if the user is a visitor</li>
	 * 	<li>Updates the user data according to were given by {@link UserDTO}:</li>
	 * 		<ul>
	 * 			<li>name</li>
	 * 			<li>password</li>
	 * 		</ul>
	 * 	<li>Save it in database</li>
	 * </ol>
	 * 
	 * @param token		  logged user identifier key
	 * @param requestBody {@link UserDTO} containing only the data to be updated
	 * @return the updated {@link User}
	 * @throws {@link PharmacyException} with status code <strong>403 (FORBIDDEN)</strong> if:
	 * 		<ul>
	 * 			<li>the logged user is deleted</li>
	 * 			<li>the logged user role is VISITOR</li>
	 * 		</ul>
	 */
	public User editBytoken(UUID token, UserDTO requestBody) {
		User userToEdit = getByToken(token);
		requestBody.setRole(userToEdit.getRole());
		requestBody.setUsername(userToEdit.getUsername());
		requestBody.setToken(userToEdit.getToken());
		
		if (userToEdit.getIsDeleted()) {
			throw new PharmacyException(Response.Status.FORBIDDEN, "Is not possible to edit this user", "User already deleted");
		}
		
		if (userToEdit.getRole().equals(Role.VISITOR)) {
			throw new PharmacyException(Response.Status.FORBIDDEN, "Is not possible to edit this user", "Wait until the administrator accept your registration before editing it");
		}
		
		userToEdit.setName(requestBody.getName());
		userToEdit.setPassword(requestBody.getPassword());
		
		userDAO.merge(userToEdit);
		
		return userToEdit;
	}


	/**
	 * Gets the amount of users that contains the CLIENT role.
	 * 
	 * @return the amount of clients
	 */
	private Short countAllClients() {
		Short amountClients = userDAO.countAllClients();
		
		if (amountClients == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		return amountClients;
	}

	/**
	 *  Gets all users that contains the VISITOR role.
	 * 
	 * @return the {@link List} of {@link User} containing all clients
	 */
	private List<UserDTO> getAllVisitors() {
		List<User> visitors = userDAO.findAllVisitors();
		List<UserDTO> visitorsDTO = userMapper.toDTOs(visitors);
		
		return visitorsDTO;
	}

	/**
	 * Mounts the administrator dashboard.
	 * <ol>
	 * 	<li>Verifies if the user that is trying to access this method is logged</li>
	 * 	<li>Verifies if the logged user has the ADMINISTRATOR role</li>
	 * 	<li>Creates a new {@link DashboardDTO} instance</li>
	 * 	<li>Sets into {@link DashboardDTO} the amount of users with CLIENT role</li>
	 * 	<li>Sets into {@link DashboardDTO} the amount of products</li>
	 * 	<li>Sets into {@link DashboardDTO} the amount of non concluded orders</li>
	 * 	<li>Sets into {@link DashboardDTO} the sum of all concluded orders</li>
	 * 	<li>Sets into {@link DashboardDTO} the sum of all concluded orders</li>
	 * 	<li>Sets into {@link DashboardDTO} the sum of all concluded orders from current month</li>
	 * 	<li>Sets into {@link DashboardDTO} the sum of all concluded orders from last month</li>
	 * 	<li>Sets into {@link DashboardDTO} the list of users with registration pending</li>
	 * </ol>
	 * 
	 * @param token logged administrator identifier key
	 * @return the {@link DashboardDTO} containing all data to be displayed filled
	 */
	public DashboardDTO dashboard(UUID token) {
		if (token == null) {
			throw new PharmacyException(Response.Status.UNAUTHORIZED, "Access denied", "It must be logged to access this feature");
		}
		
		if (!verifyIfIsAdmin(token)) {
			throw new PharmacyException(Response.Status.FORBIDDEN, "Access denied", "This feature is only available to administrators");
		}
		
		DashboardDTO dashboardDTO = new DashboardDTO();
		dashboardDTO.setTotalClients(countAllClients());
		dashboardDTO.setTotalProducts(productService.countAll());
		dashboardDTO.setTotalCarts(orderService.countAllNonConcluded());
		dashboardDTO.setTotalValueConcludedOrders(orderService.sumTotalValue());
		dashboardDTO.setTotalValueConcludedOrdersCurrentMonth(orderService.sumTotalValueConcludedOrdersCurrentMonth());
		dashboardDTO.setTotalValueConcludedOrdersLastMonth(orderService.sumTotalValueConcludedOrdersLastMonth());
		dashboardDTO.setVisitorsDTO(getAllVisitors());
		
		return dashboardDTO;
	}
}