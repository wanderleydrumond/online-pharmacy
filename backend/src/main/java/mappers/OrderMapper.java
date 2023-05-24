package mappers;

import java.util.List;
import java.util.UUID;
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
	private ProductMapper productMapper;
	
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
		
		return toDTO(order, false, null);
	}
	
	/**
	 * <p>Changes a {@link Order} object into a {@link OrderDTO} object.</p>
	 * <p><em>{@linkplain OrderMapper#toDTO(Order) toDTO} overload method</em></p>
	 * 
	 * @param order 				 the object that will be transformed into DTO object
	 * @param verifyLikedOrFavorited it will check if this product was liked and/or favorited?
	 * @param token					 logged user identifier key
	 * @return the DTO resultant object
	 */
	public OrderDTO toDTO(Order order, boolean verifyLikedOrFavorited, UUID token) {
		List<ProductDTO> productsDTO;
		
		if(verifyLikedOrFavorited && token!= null) {
			productsDTO = order.getProductsOfAnOrder().stream().map(productElement -> productMapper.toDTO(productElement, verifyLikedOrFavorited, token)).collect(Collectors.toList());
		} else {
			productsDTO = order.getProductsOfAnOrder().stream().map(productMapper::toDTO).collect(Collectors.toList());			
		}
		
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