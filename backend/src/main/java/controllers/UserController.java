package controllers;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dtos.DashboardDTO;
import dtos.UserDTO;
import entities.User;
import exceptions.PharmacyException;
import mappers.UserMapper;
import services.UserService;

/**
 * Class that contains all requisition methods that refers to user.
 * 
 * @author Wanderley Drumond
 */
@Path("/user")
public class UserController {
	/**
	 * Object that contains all user service methods.
	 */
	@Inject
	private UserService userService;
	
	/**
	 * Object that contains method that allows to switch between {@link User} and {@link UserDTO}.
	 */
	@Inject
	private UserMapper userMapper;
	
	/**
	 * Registers a new user in the system.
	 * 
	 * @param userDTO contains basic data to create the user (name, username, password and role)
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user was registered successfully</li>
	 *         <li><strong>409 (CONFLICT)</strong> if already exists an user with the same credentials</li>
	 *      </ul>
	 */
	@Path("/signup")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signUp(UserDTO userDTO) {
		try {
			User user = userMapper.toEntity(userDTO);
			
			return Response.ok(userService.signUp(user)).build();
		} catch (PharmacyException pharmacyException) {
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
	        }
	}
	
	/**
	 * Signs a user into the system.
	 * 
	 * @param username the username of the user
	 * @param password the password of the user
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user was signed in successfully</li>
	 *         <li><strong>401 (UNAUTHORISED)</strong> if the username or password is incorrect</li>
	 *         <li><strong>400 (BAD REQUEST)</strong> if the username or password are null</li>
	 *      </ul>
	 */
	@Path("/signin")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response signIn(@HeaderParam("username") String username, @HeaderParam("password") String password) {
		try {
			return Response.ok(userService.signIn(username, password)).build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in signIn() in UserController");
			pharmacyException.printStackTrace();
			
			return Response.status(pharmacyException.getHttpStatus()).header("Problem in database", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * Signs out the logged user from the system.
	 * 
	 * @param token logged user identifier key
	 * @return {@link Response} with status code:
	 * 		<ul>
	 * 			<li>200 (OK) along with true, if the user was signed out</li>
	 * 			<li>400 (BAD REQUEST) if:</li>
	 * 			<ul>
	 * 				<li>the user was already signed out</li>
	 * 				<li>the given token is null</li>
	 * 			</ul>
	 * 		</ul>
	 */
	@Path("/signout")
	@POST
	public Response signOut(@HeaderParam("token") UUID token) {
		try {
			return Response.ok(userService.signOut(token)).build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in signOut() in UserController");
			pharmacyException.printStackTrace();
			
			return Response.status(pharmacyException.getHttpStatus()).header("Request not done", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * <p>Approves the user that owns the given id in the system.</p>
	 * <p><em>Changes their role from VISITOR to CLIENT</em></p>
	 * 
	 * @param token			  logged user identifier key
	 * @param userToApproveId id of the user to be approved
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user was approved successfully</li>
	 *         <li><strong>400 (BAD REQUEST)</strong> if the user was not approved</li>
	 *         <li><strong>403 (FORBIDDEN)</strong> if the user is not an administrator</li>
	 *      </ul>
	 */
	@Path("/approve")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response approve(@HeaderParam("token") UUID token, @QueryParam("id") String userToApproveId) {
		try {
			return Response.ok(userService.approve(token, Integer.valueOf(userToApproveId))).build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in approve() in UserController");
			pharmacyException.printStackTrace();
			
			return Response.status(pharmacyException.getHttpStatus()).header("Request not done", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * Gets the logged user data.
	 * 
	 * @param token logged user identifier key
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user was found</li>
	 *         <li><strong>404 (NOT FOUND)</strong> if the given token does not exists in database</li>
	 *      </ul>
	 */
	@Path("/data")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(@HeaderParam("token") UUID token) {
		try {
			return Response.ok(userMapper.toDTO(userService.getByToken(token))).build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in getData() in UserController");
			pharmacyException.printStackTrace();
			
			return Response.status(pharmacyException.getHttpStatus()).header("Request not done", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * Edits the name and/or password from the logged user.
	 * 
	 * @param token		  logged user identifier key
	 * @param requestBody {@link UserDTO} containing only the data to be updated
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user was successfully updated</li>
	 *         <li><strong>403 (FORBIDDEN)</strong> if :</li>
	 *         	<ul>
	 *         		<li>the logged user is deleted</li>
	 *         		<li>the logged user role is a visitor</li>
	 *         	</ul>
	 *      </ul>
	 */
	@Path("/data")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editData(@HeaderParam("token") UUID token, UserDTO requestBody) {
		try {
			return Response.ok(userMapper.toDTO(userService.editBytoken(token, requestBody))).build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in editData() in UserController");
			pharmacyException.printStackTrace();
			
			return Response.status(pharmacyException.getHttpStatus()).header("Request not done", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * Gets some informations that will be displayed in the administrator's dashboard.
	 * 
	 * @param token logged administrator identifier key
	 * @return the {@link DashboardDTO} containing all data to be displayed filled
	 */
	@Path("/dashboard")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response dashboard(@HeaderParam("token") UUID token) {
		return Response.ok(userService.dashboard(token)).build();
	}
}