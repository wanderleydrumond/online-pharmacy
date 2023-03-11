package daos;

import javax.ejb.Stateless;

import entities.Comment;

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
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	public CommentDAO() {
		super(Comment.class);
	}

}
