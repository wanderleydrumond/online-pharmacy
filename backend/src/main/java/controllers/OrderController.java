package controllers;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import daos.OrderDAO;
import dtos.OrderDTO;
import entities.Order;
import mappers.OrderMapper;
import services.OrderService;

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
	 * Object that contains method that allows to switch between {@link Order} and {@link OrderDAO}.
	 */
	@Inject
	private OrderMapper orderMapper; 
	
	/**
	 * Adds a new item to the cart.
	 * 
	 * @param token		logged user identifier key
	 * @param productId	primary key that identifies the product to add to the current order
	 * @return {@link Response} with status code <strong>200 (OK)</strong> if the order was created along with the {@link OrderDTO}
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
}