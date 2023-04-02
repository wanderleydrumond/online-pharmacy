package controllers;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import daos.OrderDAO;
import dtos.OrderDTO;
import entities.Order;
import entities.Product;
import mappers.OrderMapper;
import services.OrderService;
import services.ProductService;

/**
 * Class that contains all requisition methods that refers to order.
 * 
 * @author Wanderley Drumond
 */
@Path("/order")
public class OrderController {
	/**
	 * Object that contains all order service methods.
	 */
	@Inject
	private OrderService orderService;
	
	/**
	 * Object that contains all product service methods.
	 */
	@Inject
	private ProductService productService;
	
	/**
	 * Object that contains method that allows to switch between {@link Order} and {@link OrderDAO}.
	 */
	@Inject
	private OrderMapper orderMapper; 
	
	/**
	 * Adds a new item to the cart.
	 * 
	 * @param token		logged user identifier key
	 * @param productId	primary key that identifies the product to add to the current order
	 * @return {@link Response} with status code
	 *  {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the order was created along with the {@link OrderDTO}</li>
	 *         <li><strong>401 (UNAUTHORIZED)</strong> if a non logged user tries to access this functionality</li>
	 *         <li><strong>404 (NOT FOUND)</strong> if invalid product id is provided</li>
	 *      </ul>
	 */
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@HeaderParam("token") UUID token, @QueryParam("id") String productId) {
		Order order = orderService.create(token, Short.valueOf(productId));
		OrderDTO orderDTO = orderMapper.toDTO(order);
		return Response.ok(orderDTO).build();
	}
	
	/**
	 * Gets an order by its id.
	 * 
	 * @param token	  logged user identifier key
	 * @param orderId primary key that identifies the order to find
	 * @return {@link Response} with status code
	 *  {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the order was found along with the {@link OrderDTO}</li>
	 *         <li><strong>401 (UNAUTHORIZED)</strong> if a non logged user tries to access this functionality</li>
	 *         <li><strong>404 (NOT FOUND)</strong> if invalid product id is provided</li>
	 *      </ul>
	 */
	@Path("/by")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@HeaderParam("token") UUID token, @QueryParam("id") String orderId) {
		Order order = orderService.getById(token, Short.valueOf(orderId));
		OrderDTO orderDTO = orderMapper.toDTO(order);
		
		return Response.ok(orderDTO).build();
	}
	
	/**
	 * 
	 * 
	 * @param token
	 * @param orderId
	 * @param productId
	 * @return
	 */
	@Path("/by")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response addProductById(@HeaderParam("token") UUID token, @QueryParam("orderId") String orderId, @QueryParam("productId") String productId) {
		Product product = productService.getById(Short.valueOf(productId));
		Order cart = orderService.getById(token, Short.valueOf(orderId));
		
		Order order = orderService.addProducts(product, cart);
		OrderDTO orderDTO = orderMapper.toDTO(order);
		
		return Response.ok(orderDTO).build();
	}
}