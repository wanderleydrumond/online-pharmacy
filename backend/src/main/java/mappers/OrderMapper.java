package mappers;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import dtos.OrderDTO;
import entities.Order;

/**
 * Class responsible by transform {@link Order} data that transits between backend and frontend.
 * 
 * @author Wanderley Drumond
 */
@Stateless
public class OrderMapper {
	/**
	 * Changes a {@link OrderDTO} object into a {@link Order} object.
	 * 
	 * @param orderDTO the object that will be transformed into Entity object
	 * @return the Entity resultant object
	 */
	public Order toEntity(OrderDTO orderDTO) {
		return new Order(orderDTO.getTotalValue());
	}
	
	/**
	 * Changes a {@link Order} object into a {@link OrderDTO} object.
	 * 
	 * @param order the object that will be transformed into DTO object
	 * @return the DTO resultant object
	 */
	public OrderDTO toDTO(Order order) {
		return new OrderDTO(order.getId(), order.getFinishedIn().toString(), order.getLastUpdate().toString(), order.getTotalValue());
	}
	
	/**
	 * Changes a {@link OrderDTO} object list into a {@link Order} objects list.
	 * 
	 * @param ordersDTO the list that will be transformed into Entity list
	 * @return the {@link Order} resultant objects list
	 */
	public List<Order> toEntities(List<OrderDTO> ordersDTO) {
		return ordersDTO.stream().map(this::toEntity).collect(Collectors.toList());
	}
	
	/**
	 * Changes a {@link Order} objects list into a {@link OrderDTO} objects list.
	 * 
	 * @param orders the list that will be transformed into DTO list
	 * @return the {@link OrderDTO} resultant objects list
	 */
	public List<OrderDTO> toDTOs(List<Order> orders) {
		return orders.stream().map(this::toDTO).collect(Collectors.toList());
	}
}