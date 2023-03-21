package services;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import daos.CommentDAO;
import dtos.CommentDTO;
import entities.Comment;
import entities.Product;
import entities.User;
import exceptions.PharmacyException;
import mappers.CommentMapper;

/**
 * Class that contains all the programmatic logic regarding the comment.
 * 
 * @author Wanderley Drumond
 */
@RequestScoped
public class CommentService implements Serializable {
	/**
	 * Object that contains all product service methods.
	 */
	@Inject
	ProductService productService;
	
	/**
	 * Object that contains method that allows to switch between {@link Comment} and {@link CommentDTO}.
	 */
	@Inject
	CommentMapper commentMapper;
	
	/**
	 * Object that contains all methods to manipulates database regarding comments table.
	 */
	@Inject
	CommentDAO commentDAO;

	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <ol>
	 * 	<li>Gets the product to be commented</li>
	 * 	<li>Transforms the comment message body content from {@link CommentDTO} to {@link Comment}</li>
	 * 	<li>Checks if already exists a comment for this product made by this user</li>
	 * 	<li>Sets the user who wrote the comment</li>
	 * 	<li>Sets the product which the comment belongs</li>
	 * 	<li>Saves the comment in the database</li>
	 * </ol>
	 * 
	 * @param user		  object that contains the data of the user who wrote the comment
	 * @param productId   primary key that identifies the product to comment
	 * @param requestBody the comment content text
	 * @return the new {@link Comment}
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 403 (FORBIDDEN) if already exists a comment for this product made by this user
	 */
	public Comment create(User user, Short productId, CommentDTO requestBody) {
		Product product = productService.getById(productId);
		Comment newComment = commentMapper.toEntity(requestBody);
		
		getByProductIdForLoggedUser(productId, user.getToken()).ifPresent(commentElement -> {
			throw new PharmacyException(Response.Status.FORBIDDEN, "This user already commented this product.", "Only one comment per product is allowed per user.");
		});
		
		newComment.setOwner(user);
		newComment.setProduct(product);
		
		commentDAO.persist(newComment);
		
		return newComment;
	}

	/**
	 * Gets the comment of a product made by the logged user.
	 * 
	 * @param productId primary key that identifies the product that have a comment
	 * @param token 	logged user identifier key
	 * @return The {@link Optional} {@link Comment} that belongs to the logged user for the identified product
	 */
	public Optional<Comment> getByProductIdForLoggedUser(Short productId, UUID token) {
		Optional<Comment> comment = commentDAO.findByProductIdForLoggedUser(productId, token);
		if (comment.isEmpty()) {
			throw new PharmacyException(Status.NOT_FOUND, "Product not found", "There is no such product for the given id"); 
		}
		
		return comment;
	}
}