package mappers;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import daos.ProductDAO;
import daos.UserDAO;
import dtos.ProductDTO;
import entities.Product;
import enums.Section;

/**
 * Class responsible by transform {@link Product} data that transits between backend and frontend.
 * 
 * @author Wanderley Drumond
 */
@Stateless
public class ProductMapper {
	
	/**
	 * Object that contains all methods to manipulates database regarding users table.
	 */
	@Inject
	private UserDAO userDAO;
	
	/**
	 * Object that contains all methods to manipulates database regarding products table.
	 */
	@Inject
	private ProductDAO productDAO;
	
	/**
	 * A generic token when a user assigned to a non logged user
	 */
	private final UUID NOT_LOGGED_TOKEN = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	/**
	 * Changes a {@link ProductDTO} object into a {@link Product} object.
	 * 
	 * @param productDTO the object that will be transformed into Entity object
	 * @return the {@link Product} resultant object
	 */
	public Product toEntity(ProductDTO productDTO) {
		return new Product(productDTO.getName(), Float.parseFloat(productDTO.getPrice().substring(0, productDTO.getPrice().length())), Section.valueOf(productDTO.getSection()), productDTO.getImage());
	}
	
	/**
	 * Changes a {@link Product} object into a {@link ProductDTO} object checking if this product was liked and or favorited.
	 * 
	 * @param product 				 the object that will be transformed into DTO object
	 * @param verifyLikedOrFavorited it will check if this product was liked and/or favorited?
	 * @param token					 logged user identifier key
	 * @return the {@link ProductDTO} resultant object
	 */
	public ProductDTO toDTO(Product product, boolean verifyLikedOrFavorited, UUID token) {
		DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
		String formatedValue = decimalFormat.format(product.getPrice()) + "€";
		if (verifyLikedOrFavorited && !token.equals(NOT_LOGGED_TOKEN)) {
			Boolean hasLiked = productDAO.verifyLoggedUserLiked(token, product);
			Boolean hasFavorited = productDAO.verifyLoggedUserFavorited(token, product);
			
			return new ProductDTO(product.getId(), userDAO.countTotalLikes(product.getId()), product.getName(), product.getImage(), product.getSection().getVALUE(), formatedValue, hasLiked, hasFavorited);
		}
		return new ProductDTO(product.getId(), userDAO.countTotalLikes(product.getId()), product.getName(), product.getImage(), product.getSection().getVALUE(), formatedValue, false, false);
	}
	
	/**
	 * Changes a {@link Product} object into a {@link ProductDTO} object <strong style="text-decoration: underline;">without</strong> checking if this product was liked and or favorited.
	 * 
	 * @param product the object that will be transformed into DTO object
	 * @return the {@link ProductDTO} resultant object
	 */
	public ProductDTO toDTO(Product product) {
		return toDTO(product, false, null);
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
	
	/** Changes a {@link Product} objects list into a {@link ProductDTO} objects list checking if this product was liked and or marked as favourite.
	 * @param products				 the list that will be transformed into DTO list
	 * @param verifyLikedOrFavorited it will check if this product was liked and/or marked as favourite?
	 * @param token					 logged user identifier key
	 * @return the {@link ProductDTO} resultant objects list
	 */
	public List<ProductDTO> toDTOs(List<Product> products, boolean verifyLikedOrFavorited, UUID token) {
		return products.stream().map(productElement -> toDTO(productElement, verifyLikedOrFavorited, token)).collect(Collectors.toList());
	}
	
	
	/**
	 * Changes a {@link Product} objects list into a {@link ProductDTO} objects list <strong style="text-decoration: underline;">without</strong>  checking if this product was liked and or favorited.
	 * 
	 * @param products the list that will be transformed into DTO list
	 * @return the {@link ProductDTO} resultant objects list
	 */
	public List<ProductDTO> toDTOs(List<Product> products) {
		return toDTOs(products, false, null);
	}
}