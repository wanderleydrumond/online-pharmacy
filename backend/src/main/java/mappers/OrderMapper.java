package mappers;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import dtos.OrderDTO;
import dtos.ProductDTO;
import entities.Order;

/**
 * Class responsible by transform {@link Order} data that transits between backend and frontend.
 * 
 * @author Wanderley Drumond
 */
@Stateless
public class OrderMapper {
	@Inject
	ProductMapper productMapper;
	
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
		List<ProductDTO> productsDTO = order.getProductsOfAnOrder().stream().map(productMapper::toDTO).collect(Collectors.toList());
		return new OrderDTO(order.getId(), order.getLastUpdate().toString(), order.getTotalValue(), order.getIsConcluded(), productsDTO);
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