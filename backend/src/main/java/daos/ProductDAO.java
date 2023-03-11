package daos;

import javax.ejb.Stateless;

import entities.Product;

/**
 * Class that makes the database communication layer role in relation with of the products table.
 * 
 * @author Wanderley Drumond
 *
 */
@Stateless
public class ProductDAO extends GenericDAO<Product> {

	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	public ProductDAO() {
		super(Product.class);
	}

}
