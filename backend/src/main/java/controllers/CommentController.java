package controllers;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dtos.CommentDTO;
import entities.Comment;
import entities.User;
import mappers.CommentMapper;
import services.CommentService;
import services.UserService;

/**
 * Class that contains all requisition methods that refers to comment.
 * 
 * @author Wanderley Drumond
 *
 */
@Path("/comment")
public class CommentController {
	/**
	 * Object that contains all comment service methods.
	 */
	@Inject
	CommentService commentService;
	
	/**
	 * Object that contains all user service methods.
	 */
	@Inject
	UserService userService;
	
	/**
	 * Object that contains method that allows to switch between {@link Comment} and {@link CommentDTO}.
	 */
	@Inject
	CommentMapper commentMapper;
	
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@HeaderParam("token") UUID token, @QueryParam("id") String productId, CommentDTO requestBody) {
		User user = userService.getByToken(token); 
		Comment comment = commentService.create(user, Short.valueOf(productId), requestBody);
		CommentDTO commentDTO = commentMapper.toDTO(comment, user);
		
		return Response.ok(commentDTO).build();
	}
}