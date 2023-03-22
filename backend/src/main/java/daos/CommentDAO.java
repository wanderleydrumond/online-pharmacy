package daos;

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
			System.err.println("Catch " + noResultException.getClass().getName() + " in findByProductIdForLoggedUser() in CommentDAO");
			Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, "in findByProductIdForLoggedUser()", noResultException);
			// noResultException.printStackTrace();
			
			return Optional.empty();
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in findByProductIdForLoggedUser() in CommentDAO");
			Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, "in findByProductIdForLoggedUser()", exception);
			// exception.printStackTrace();
			
			return null;
		}
	}

}
