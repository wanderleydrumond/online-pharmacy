package services;

import java.io.Serializable;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import daos.ProductDAO;
import dtos.ProductDTO;
import entities.Product;
import exceptions.PharmacyException;
import mappers.ProductMapper;

/**
 * Class that contains all the programmatic logic regarding the product.
 * 
 * @author Wanderley Drumond
 */
@RequestScoped
public class ProductService implements Serializable {
	/**
	 * Object that contains all user service methods.
	 */
	@Inject
	private UserService userService;
	
	/**
	 * Object that contains all product service methods.
	 */
	@Inject
	ProductDAO productDAO;
	
	/**
	 * Object that contains method that allows to switch between {@link Product} and {@link ProductDTO}.
	 */
	@Inject
	ProductMapper productMapper;

	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <ol>
	 * 	<li>Verifies if the logged user has the ADMINISTRATOR role.</li>
	 * 	<li>Transforms the provided {@link ProductDTO} into {@link Product}.</li>
	 * 	<li>Saves the {@link Product} in the database.</li>
	 * </ol>
	 * 
	 * @param token		  logged user identifier key
	 * @param requestBody the product data
	 * @return the new {@link Product} created
	 */
	public Product create(UUID token, ProductDTO requestBody) {
		Boolean isAdmin = userService.verifyIfIsAdmin(token);
		
		if (!isAdmin) {
			throw new PharmacyException(Response.Status.FORBIDDEN, "insufficient privileges", "Only administrators can execute this action");
		}
		
		Product newProduct = productMapper.toEntity(requestBody);
		
		productDAO.persist(newProduct);
		
		return newProduct;
	}

}