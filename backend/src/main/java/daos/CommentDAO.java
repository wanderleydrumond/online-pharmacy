package daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import entities.Comment;
import entities.Product;
import entities.User;

/**
 * Class that makes the database communication layer role in relation with of the comments table.
 * 
 * @author Wanderley Drumond
 *
 */
@Stateless
public class CommentDAO extends GenericDAO<Comment> {

	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	public CommentDAO() {
		super(Comment.class);
	}

	/**
	 * Finds the comment from the identified product of the logged user.
	 * 
	 * @param productId	primary key that identifies the product that have a comment
	 * @param token		logged user identifier key
	 * @return
	 * 		<ul>
	 * 			<li>The {@link Optional} {@link Comment} that belongs to the logged user for the identified product</li>
	 * 			<li>{@link Optional} empty if no comment was found with the given match</li>
	 * 			<li>null, if some problem happened in database</li>
	 * 		</ul>
	 */
	public Optional<Comment> findByProductIdForLoggedUser(Short productId, UUID token) {
		try {
			final CriteriaQuery<Comment> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Comment.class);
			Root<Comment> commentTable = CRITERIA_QUERY.from(Comment.class);
			Join<Comment, User> userTable = commentTable.join("owner");
			Join<Comment, Product> productTable = commentTable.join("product");
			
			CRITERIA_QUERY.select(commentTable).where(criteriaBuilder.and(
					criteriaBuilder.equal(userTable.get("token"), token), 
					criteriaBuilder.equal(productTable.get("id"), productId)));
			
			return Optional.ofNullable(entityManager.createQuery(CRITERIA_QUERY).getSingleResult());
		} catch (NoResultException noResultException) {
			Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, "in findByProductIdForLoggedUser()", noResultException);
			
			return Optional.empty();
		} catch (Exception exception) {
			Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, "in findByProductIdForLoggedUser()", exception);
			
			return null;
		}
	}

	/**
	 * Finds the list of comments made from all users from the given product.
	 * 
	 * @param productId	primary key that identifies the product to find all comments
	 * @return
	 * 		<ul>
	 * 			<li>The {@link Comment} {@link List} that belongs to the identified product</li>
	 * 			<li>new {@link ArrayList} if no comment was found with the given match</li>
	 * 			<li>null, if some problem happened in database</li>
	 * 		</ul>
	 */
	public List<Comment> findAllByProductId(Short productId) {
		try {
			final CriteriaQuery<Comment> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Comment.class);
			Root<Comment> commentTable = CRITERIA_QUERY.from(Comment.class);
			Join<Comment, Product> productTable = commentTable.join("product");
			
			CRITERIA_QUERY.select(commentTable).where(criteriaBuilder.equal(productTable.get("id"), productId));
			
			return entityManager.createQuery(CRITERIA_QUERY).getResultList();
		} catch (NoResultException noResultException) {
			Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, "in findAllByProductId()", noResultException);
			
			return new ArrayList<Comment>();
		} catch (Exception exception) {
			Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, "in findAllByProductId()", exception);
			
			return null;
		}
	}
}