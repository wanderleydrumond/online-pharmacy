package services;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import daos.CommentDAO;
import dtos.CommentDTO;
import entities.Comment;
import entities.Product;
import entities.User;
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

	public Comment create(User user, Short productId, CommentDTO requestBody) {
		Product product = productService.getById(productId);
		Comment newComment = commentMapper.toEntity(requestBody);
		
		// FIXME check if already have a comment for this product by this user
		newComment.setOwner(user);
		newComment.setProduct(product);
		
		commentDAO.persist(newComment);
		
		return newComment;
	}

}