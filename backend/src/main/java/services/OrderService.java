package services;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import daos.OrderDAO;
import daos.ProductDAO;
import entities.Order;
import entities.Product;
import entities.User;
import exceptions.PharmacyException;

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
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 401 (UNAUTHORIZED) if a non logged user tries to access this functionality
	 */
	public synchronized Order create(UUID token, Short productId) {
		
		if (token == null) {
			throw new PharmacyException(Response.Status.UNAUTHORIZED, "User not logged", "User must be logged to access this functionality");
		}
		
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
	public Order addProducts(Product product, Order order) {
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

	/**
	 * <ol>
	 * 	<li>Gets the {@link Order} inside of an {@link Optional}</li>
	 * 	<li>Checks if order is null</li>
	 * 	<li>Checks if order is empty</li>
	 * 	<li>Gets all products which this order possess</li>
	 * 	<li>Updates the order products list with above found</li>
	 * </ol>
	 * 
	 * @param token	  logged user identifier key
	 * @param orderId primary key that identifies the order to find
	 * @return The found {@link Order}
	 */
	public Order getById(UUID token, Short orderId) {
		Optional<Order> order = orderDAO.findById(orderId);
		
		if (order == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		if (order.isEmpty()) {
			throw new PharmacyException(Response.Status.NO_CONTENT, "No content", "No order found with the provided id");
		}
		
		List<Product> productsOfThisOrder = productService.getAllByOrderId(orderId);
		
		order.get().setProductsOfAnOrder(productsOfThisOrder);
		
		return order.get();
	}

	/**
	 * <ol>
	 * 	<li>Gets the {@link Order} to be updated</li>
	 * 	<li>Gets the {@link Product} to remove</li>
	 * 	<li>Remove the product from the order</li>
	 * 	<li>Sets the order total value conditionally</li>
	 * 	<li>Updates the order in the database</li>
	 * </ol>
	 * 
	 * @param token		logged user identifier key
	 * @param orderId	primary key that identifies the order to find
	 * @param productId	primary key that identifies the product to remove to the current order
	 * @return the updated {@link Order}
	 */
	public Order removeProductByOrderId(UUID token, Short orderId, Short productId) {
		Order order = getById(token, orderId);
		
		Product product = order.getProductsOfAnOrder()
			    .stream()
			    .filter(productElement -> productElement.getId().equals(productId))
			    .findFirst()
			    .orElse(null);

		order.getProductsOfAnOrder().remove(product);
		
		if (order.getTotalValue() == null) {
			order.setTotalValue(product.getPrice());
		} else {
			order.setTotalValue(order.getTotalValue() - product.getPrice());
		}
		
		orderDAO.merge(order);
		
		return order;
	}
}