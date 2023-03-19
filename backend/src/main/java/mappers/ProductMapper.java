package mappers;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import daos.UserDAO;
import dtos.ProductDTO;
import entities.Product;

/**
 * Class responsible by transform {@link Product} data that transits between backend and frontend.
 * 
 * @author Wanderley Drumond
 */
@Stateless
public class ProductMapper {
	
	/**
	 * Object that contains all user service methods.
	 */
	@Inject
	UserDAO userDAO;
	
	/**
	 * Changes a {@link ProductDTO} object into a {@link Product} object.
	 * 
	 * @param productDTO the object that will be transformed into Entity object
	 * @return the {@link Product} resultant object
	 */
	public Product toEntity(ProductDTO productDTO) {
		return new Product(productDTO.getName(), productDTO.getPrice(), productDTO.getSection(), productDTO.getImage());
	}
	
	/**
	 * Changes a {@link Product} object into a {@link ProductDTO} object.
	 * 
	 * @param product the object that will be transformed into DTO object
	 * @return the {@link ProductDTO} resultant object
	 */
	public ProductDTO toDTO(Product product) {
		return new ProductDTO(product.getId(), userDAO.countTotalLikes(product.getId()), product.getName(), product.getImage(), product.getPrice(), product.getSection());
	}
	
	/**
	 * Changes a {@link ProductDTO} object list into a {@link Product} objects list.
	 * 
	 * @param productDTO the list that will be transformed into Entity list
	 * @return the {@link Product} Entity resultant objects list
	 */
	public List<Product> toEntities(List<ProductDTO> productsDTO) {
		return productsDTO.stream().map(this::toEntity).collect(Collectors.toList());
	}
	
	/**
	 * Changes a {@link Product} objects list into a {@link ProductDTO} objects list.
	 * 
	 * @param products the list that will be transformed into DTO list
	 * @return the {@link ProductDTO} resultant objects list
	 */
	public List<ProductDTO> toDTOs(List<Product> products) {
		return products.stream().map(this::toDTO).collect(Collectors.toList());
	}
}