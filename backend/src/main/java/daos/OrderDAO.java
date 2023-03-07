package daos;

import javax.ejb.Stateless;

import entities.Order;

/**
 * Class that makes the database communication layer role in relation with of the orders table.
 * 
 * @author Wanderley Drumond
 *
 */
@Stateless
public class OrderDAO extends GenericDAO<Order> {

	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;

	public OrderDAO() {
		super(Order.class);
	}

}
