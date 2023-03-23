package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
	private CommentService commentService;
	
	/**
	 * Object that contains all user service methods.
	 */
	@Inject
	private UserService userService;
	
	/**
	 * Object that contains method that allows to switch between {@link Comment} and {@link CommentDTO}.
	 */
	@Inject
	private CommentMapper commentMapper;
	
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
	 *         <li><strong>404 (NOT FOUND)</strong> if an invalid product id was provided</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/by")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getByProductIdForLoggedUser(@HeaderParam("token") UUID token, @QueryParam("id") String productId) {
		User user = userService.getByToken(token);
		Comment comment = commentService.getByProductIdForLoggedUser(Short.valueOf(productId), token).get();
		
		return Response.ok(commentMapper.toDTO(comment, user)).build();
	}
	
	/**
	 * Removes a single comment its provided id.
	 * 
	 * @param token		logged user identifier key
	 * @param commentId primary key that identifies the comment to remove
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the comment was found along with the true boolean value</li>
	 *         <li><strong>403 (FORBIDDEN)</strong> if the comment does not belongs to the logged user</li>
	 *      </ul>
	 */
	@Path("/by")
	@DELETE
	public Response deleteById(@HeaderParam("token") UUID token, @QueryParam("id") String commentId) {
		return Response.ok(commentService.delete(token, Short.valueOf(commentId))).build();
	}
	
	/**
	 * @param token		  logged user identifier key
	 * @param commentId	  primary key that identifies the comment to update
	 * @param requestBody comment content text
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the comment was found along with the {@link CommentDTO} which contains the updated comment content text</li>
	 *         <li><strong>403 (FORBIDDEN)</strong> if the comment does not belongs to the logged user</li>
	 *      </ul>
	 */
	@Path("/by")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateById(@HeaderParam("token") UUID token, @QueryParam("id") String commentId, CommentDTO requestBody) {
		User user = userService.getByToken(token);
		Comment comment = commentService.updateById(token, Short.valueOf(commentId), requestBody);
		CommentDTO answerBody = commentMapper.toDTO(comment, user);
		
		return Response.ok(answerBody).build();
	}
	
	/**
	 * Gets all comments made from all users of the given product.
	 * 
	 * @param productId primary key that identifies the product to find all comments
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the product was found along with the {@link CommentDTO} {@link List} from it</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("all-by")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllByProductId(@QueryParam("id") String productId) {
		List<Comment> comments = commentService.getAllByProductId(Short.valueOf(productId));
		List<CommentDTO> commentsDTO = new ArrayList<>();
		
		/*
for (Comment commentElement : comments) {
			CommentDTO commentDTO = commentMapper.toDTO(commentElement, commentElement.getOwner());
			commentsDTO.add(commentDTO);
		}
*/
		comments.forEach(commentElement -> commentsDTO.add(commentMapper.toDTO(commentElement, commentElement.getOwner())));
		
		return Response.ok(commentsDTO).build();
	}
}