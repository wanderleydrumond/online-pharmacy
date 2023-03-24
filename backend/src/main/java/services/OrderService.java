package services;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import daos.OrderDAO;
import daos.ProductDAO;
import entities.Order;
import entities.Product;
import entities.User;

/**
 * Class that contains all the programmatic logic regarding the order.
 * 
 * @author Wanderley Drumond
 */
@RequestScoped
public class OrderService implements Serializable {
	/**
	 * Object that contains all user service methods.
	 */
	@Inject
	private UserService userService;
	
	/**
	 * Object that contains all product service methods.
	 */
	@Inject
	private ProductService productService;
	
	/**
	 * Object that contains all order service methods.
	 */
	@Inject
	private OrderDAO orderDAO;
	
	/**
	 * Object that contains all product service methods.
	 */
	@Inject
	private ProductDAO productDAO;
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <ol>
	 * 	<li>Gets the logged user who will make the order</li>
	 * 	<li>Gets the product to be bought</li>
	 * 	<li>Creates a new {@link Order}</li>
	 * 	<li>Sets the order buyer</li>
	 * 	<li>Sets the order status</li>
	 * 	<li>Saves the order in the database</li>
	 * </ol>
	 * <p><em>Ensures that the whole code procedure is finished before the {@linkplain OrderService#addProducts addProducts} is called</em></p>
	 * 
	 * @param token		logged user identifier key
	 * @param productId	primary key that identifies the product to add to the current order
	 * @return {@linkplain OrderService#addProducts addProducts} call
	 */
	public synchronized Order create(UUID token, Short productId) {
		User buyer = userService.getByToken(token);
		Product product = productService.getById(productId);
		Order order = new Order();
		
		order.setBuyer(buyer);
		order.setIsConcluded(false);
		
		orderDAO.persist(order);
		return addProducts(product, order);
	}
	
	/**
	 * <ol>
	 * 	<li>Gets the products list of the current order</li>
	 * 	<li>Adds the given {@link Product} to the current {@link Order} {@link List}</li>
	 * 	<li>Sets the order total value conditionally</li>
	 * 	<li>Updates the order in the database</li>
	 * </ol>
	 * 
	 * @param product the {@link Product} to add
	 * @param order the {@link Order} to update
	 * @return the updated {@link Order}
	 */
	private Order addProducts(Product product, Order order) {
		List<Product> productsInThisCart = productDAO.findAllByOrderId(order.getId());
		
		productsInThisCart.add(product);
		order.setProductsOfAnOrder(productsInThisCart);
		
		if (order.getTotalValue() == null) {
			order.setTotalValue(product.getPrice());
		} else {
			order.setTotalValue(product.getPrice() + order.getTotalValue());
		}
		orderDAO.merge(order);
		
		return order;
	}

}