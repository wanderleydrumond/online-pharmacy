package controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	UserService userService;
	
	/**
	 * Object that contains methods from {@link User} object to switch it between Entity and DTO formats.
	 */
	@Inject
	UserMapper userMapper;
	
	@Path("/signup")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signUp(UserDTO userDTO) {
		try {
			User user = userMapper.toEntity(userDTO);
			// Se eu quisesse retornar uma lista do UserService:
			// return Response.ok(userService.signUp(user).stream().map(userMapper::toDTO).collect(Collectors.toList())).build(); // Sem usar um método
			// return Response.ok(userMapper.toDTOs(userService.signUp(user))).build();	
					
			// return Response.ok(userService.signUp(user)).build();
			
			return Response.ok(userService.signUp(user)).build();
		} catch (PharmacyException pharmacyException) {
			return Response.status(pharmacyException.getHttpStatus()).header("Bad Request", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
	        }
	}
}