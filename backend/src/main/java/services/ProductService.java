package services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import daos.ProductDAO;
import dtos.ProductDTO;
import entities.Product;
import enums.Section;
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
	private ProductDAO productDAO;
	
	/**
	 * Object that contains method that allows to switch between {@link Product} and {@link ProductDTO}.
	 */
	@Inject
	private ProductMapper productMapper;

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

	/**
	 * <ol>
	 * 	<li>Gets the list of products of the provided section</li>
	 * 	<li>Checks if the product list is null</li>
	 * 	<li>Checks if the product list is empty</li>
	 * </ol>
	 * 
	 * @param section which the list of products belongs
	 * @return the products list from the provided section
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 404 (NOT FOUND) if the provided enumerator value does not exists in database
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 502 (BAD GATEWAY) if some problem happened in database
	 */
	public List<Product> getAllBySection(String section) {
		boolean exists = false;
		for (Section sectionElement : Section.values()) {
			if (sectionElement.name().equals(section)) {
				exists = true;
			}
		}
		
		if (!exists) {
			throw new PharmacyException(Response.Status.NOT_FOUND, "Section not found", "This section does not exists in our database, please try again with another value");
		}

		List<Product> productsOfThisSection = productDAO.findAllBySection(Section.valueOf(section));
		
		if (productsOfThisSection == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		if (productsOfThisSection.isEmpty()) {
			throw new PharmacyException(Response.Status.NO_CONTENT, "Empty section", "This section do not have any products yet");
		}
		
		return productsOfThisSection;
	}

	/**
	 * <ol>
	 * 	<li>Gets all product sections.</li>
	 * 	<li>Checks if section list is null</li>
	 *  <li>Checks if the section list is empty</li>
	 * </ol>
	 * 
	 * @return the {@link Product} {@link ArrayList} with all sections inside of it
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 502 (BAD GATEWAY) if some problem happened in database
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 202 (NO CONTENT) if the {@link ArrayList} has no elements inside
	 */
	public List<Section> getAllSections() {
		List<Section> sections = productDAO.findAllSections();
		
		if (sections == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		if (sections.isEmpty()) {
			throw new PharmacyException(Response.Status.NO_CONTENT, "No content", "There are no sections yet");
		}
		
		return sections;
	}

	/**
	 * <ol>
	 * 	<li>Gets all product.</li>
	 * 	<li>Checks if products list is null.</li>
	 *  <li>Checks if the products list is empty.</li>
	 * </ol>
	 * 
	 * @return the {@link Product} {@link ArrayList} with all products inside of it
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 502 (BAD GATEWAY) if some problem happened in database
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 202 (NO CONTENT) if the {@link ArrayList} has no elements inside
	 */
	public List<Product> getAll() {
		List<Product> products = productDAO.findAll();
		
		if (products == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		if (products.isEmpty()) {
			throw new PharmacyException(Response.Status.NO_CONTENT, "No content", "There are no sections yet");
		}
		
		return products;
	}
}