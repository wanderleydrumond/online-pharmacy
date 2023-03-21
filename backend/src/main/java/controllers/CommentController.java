package controllers;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dtos.CommentDTO;
import dtos.ProductDTO;
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
	
	/**
	 * Creates a single comment for the provided product for the logged user.
	 * 
	 * @param token		  logged user identifier key
	 * @param productId	  primary key that identifies the product to comment
	 * @param requestBody the comment content text
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the comment was created along with the {@link ProductDTO} {@link List}</li>
	 *         <li><strong>403 (FORBIDDEN)</strong> if already exists a comment for this product made by this user</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
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
	
	
	/**
	 * <p>Gets the comment of a product made by the logged user.</p>
	 * <p><em>Out of scope. Only the service layer was requested.</em></p>
	 * 
	 * @param token		logged user identifier key
	 * @param productId primary key that identifies the product that have a comment
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the comment was found along with the {@link ProductDTO}</li>
	 *         <li><strong>404 (NOT FOUND)</strong> if if an invalid product id was provided</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/by")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getByProductIdForLoggedUser(@HeaderParam("token") UUID token, @QueryParam("id") String productId) {
		User user = userService.getByToken(token);
		Comment comment = commentService.getByProductIdForLoggedUser(Short.valueOf(productId), token).get();
		// Se fosse para retornar uma lista de comentários DTO
		// List<CommentDTO> commentsDTO = comment.stream().map(commentElement -> commentMapper.toDTO(commentElement, user)).collect(Collectors.toList());
		return Response.ok(commentMapper.toDTO(comment, user)).build();
	}
}