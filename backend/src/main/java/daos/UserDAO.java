package daos;

import javax.ejb.Stateless;

import entities.User;

/**
 * Class that makes the database communication layer role in relation with of the users table.
 * 
 * @author Wanderley Drumond
 *
 */
@Stateless
public class UserDAO extends GenericDAO<User> {
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;

	public UserDAO() {
		super(User.class);
	}
}