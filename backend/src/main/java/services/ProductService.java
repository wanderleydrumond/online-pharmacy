package services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import daos.ProductDAO;
import daos.UserDAO;
import dtos.ProductDTO;
import entities.Product;
import entities.User;
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
	 * Object that contains all methods to manipulates database regarding products table.
	 */
	@Inject
	private ProductDAO productDAO;
	
	/**
	 * Object that contains all methods to manipulates database regarding users table.
	 */
	@Inject
	private UserDAO userDAO;
	
	/**
	 * Object that contains method that allows to switch between {@link Product} and {@link ProductDTO}.
	 */
	@Inject
	private ProductMapper productMapper;

	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
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
		
		return productsOfThisSection;
	}

	/**
	 * Adds all enumeration values inside a new {@link String} {@link ArrayList}.
	 * 
	 * @return the {@link String} {@link ArrayList} with all section values inside of it
	 */
	public List<String> getAllSections() {
		List<String> descriptions = new ArrayList<>();
		for (Section section : Section.values()) {
			descriptions.add(section.getVALUE());
		}
		
		return descriptions;
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
	 */
	public List<Product> getAll() {
		List<Product> products = productDAO.findAll();
		
		if (products == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		return products;
	}

	/**
	 * <ol>
	 * 	<li>Gets the {@link Product} by its id.</li>
	 * 	<li>Checks if product is null.</li>
	 *  <li>Checks if the product is empty.</li>
	 * </ol>
	 * 
	 * @param productId primary key that identifies the product to be found
	 * @return the {@link Product} that owns the provided id
	 */
	public Product getById(Short productId) {
		Optional<Product> productFound = productDAO.findById(productId);
		
		if (productFound == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		if (productFound.isEmpty()) {
			throw new PharmacyException(Response.Status.NOT_FOUND, "Product not found", "There is not exists a product with the provided id");
		}
		
		return productFound.get();
	}

	/**
	 * <ol>
	 * 	<li>Gets the user who will do the like by their token.</li>
	 * 	<li>Gets the product to like by its id.</li>
	 * 	<li>Gets the list of users that liked this product. <em>({@link Product} attribute)</em> </li>
	 * 	<li>Checks if this list is null.</li>
	 * 	<li>Adds the logged user to this list.</li>
	 * 	<li>Sets list of users that liked this product with the above list</li>
	 * 	<li>Save the product in database</li>
	 * </ol>
	 * 
	 * @param token		logged user identifier key
	 * @param productId	primary key that identifies the product to like
	 * @return true if the product was successfully saved
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 502 (BAD GATEWAY) if some problem happened in database
	 */
	public boolean likeById(UUID token, Short productId) {
		User userWhoLikedTheProduct = userService.getByToken(token);
		Product productToBeLiked = getById(productId);
		List<User> usersThatLikedThisProduct = userDAO.findAllThatLikedThisProduct(productId);
		
		if (usersThatLikedThisProduct == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		usersThatLikedThisProduct.add(userWhoLikedTheProduct);
		productToBeLiked.setUsersThatLiked(usersThatLikedThisProduct);
		
		try {
			productDAO.merge(productToBeLiked);
			
			return true;
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in likeById() in ProductService");
			exception.printStackTrace();
			throw new PharmacyException(Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
	}

	/**
	 * <ol>
	 * 	<li>Gets the user who will do the unlike by their token.</li>
	 * 	<li>Gets the product to unlike by its id.</li>
	 * 	<li>Gets the list of users that liked this product. <em>({@link Product} attribute)</em> </li>
	 * 	<li>Checks if this list is null.</li>
	 * 	<li>Adds the logged user to this list.</li>
	 * 	<li>Sets list of users that liked this product with the above list</li>
	 * 	<li>Save the product in database</li>
	 * </ol>
	 * 
	 * @param token		logged user identifier key
	 * @param productId	primary key that identifies the product to like
	 * @return true if the product was successfully saved
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 502 (BAD GATEWAY) if some problem happened in database
	 */
	public boolean unlikeById(UUID token, Short productId) {
		User userWhoUnlikedTheProduct = userService.getByToken(token);
		Product productToBeUnliked = getById(productId);
		List<User> usersThatLikedThisProduct = userDAO.findAllThatLikedThisProduct(productId);
		
		if (usersThatLikedThisProduct == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		usersThatLikedThisProduct.removeIf(userElement -> userWhoUnlikedTheProduct.getId().equals(userElement.getId()));
		productToBeUnliked.setUsersThatLiked(usersThatLikedThisProduct);
		
		try {
			productDAO.merge(productToBeUnliked);
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in likeById() in ProductService");
			exception.printStackTrace();
			throw new PharmacyException(Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		return true;
	}

	/**
	 * <ol>
	 * 	<li>Gets the user who will favourited by their token.</li>
	 * 	<li>Gets the product to be favourite by its id.</li>
	 * 	<li>Gets the list of users that favourited this product.<em>({@link Product} attribute)</em> </li>
	 * 	<li>Checks if this list is null.</li>
	 * 	<li>Adds the logged user to this list.</li>
	 * 	<li>Sets list of users that favourited this product with the above list</li>
	 * 	<li>Save the product in database</li>
	 * </ol>
	 * 
	 * @param token		logged user identifier key
	 * @param productId	primary key that identifies the product to be favourited
	 * @return true if the product was successfully saved
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 502 (BAD GATEWAY) if some problem happened in database
	 */
	public boolean favoriteById(UUID token, Short productId) {
		User userWhoFavouritedTheProduct = userService.getByToken(token);
		Product productToBeFavourited = getById(productId);
		List<User> usersThatFavouritedThisProduct = userDAO.findAllThatFavouritedThisProduct(productId);
		
		if (usersThatFavouritedThisProduct == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		usersThatFavouritedThisProduct.add(userWhoFavouritedTheProduct);
		productToBeFavourited.setUsersThatFavorited(usersThatFavouritedThisProduct);
		
		try {
			productDAO.merge(productToBeFavourited);
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in favoriteById() in ProductService");
			exception.printStackTrace();
			throw new PharmacyException(Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		return true;
	}

	/**
	 * <ol>
	 * 	<li>Gets the user who will unfavourited by their token.</li>
	 * 	<li>Gets the product to be favourite by its id.</li>
	 * 	<li>Gets the list of users that favourited this product.<em>({@link Product} attribute)</em> </li>
	 * 	<li>Checks if this list is null.</li>
	 * 	<li>Adds the logged user to this list.</li>
	 * 	<li>Sets list of users that unfavourited this product with the above list</li>
	 * 	<li>Save the product in database</li>
	 * </ol>
	 * 
	 * @param token		logged user identifier key
	 * @param productId	primary key that identifies the product to be unfavourited
	 * @return true if the product was successfully saved
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 502 (BAD GATEWAY) if some problem happened in database
	 */
	public boolean unfavoriteById(UUID token, Short productId) {
		User userWhoUnfavouritedTheProduct = userService.getByToken(token);
		Product productToBeUnfavourited = getById(productId);
		List<User> usersThatUnfavouritedThisProduct = userDAO.findAllThatFavouritedThisProduct(productId);
		
		if (usersThatUnfavouritedThisProduct == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		usersThatUnfavouritedThisProduct.removeIf(userElement -> userWhoUnfavouritedTheProduct.getId().equals(userElement.getId()));
		productToBeUnfavourited.setUsersThatFavorited(usersThatUnfavouritedThisProduct);
		
		try {
			productDAO.merge(productToBeUnfavourited);
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in unfavoriteById() in ProductService");
			exception.printStackTrace();
			throw new PharmacyException(Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		return true;
	}

	/**
	 * Gets all favourite products from the logged user.
	 * 
	 * @param token	logged user identifier key
	 * @return the logged user favourite {@link Product} {@link List}
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 401 (UNAUTHORISED) if token is null
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 502 (BAD GATEWAY) if some problem happened in database
	 */
	public List<Product> getAllFavoritesByToken(UUID token) {
		if (token == null) {
			throw new PharmacyException(Response.Status.UNAUTHORIZED, "User not logged", "User must be logged to access this functionality");
		}
		
		List<Product> favouriteProducts = productDAO.findAllfavoritesByToken(token);
		
		if (favouriteProducts == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		return favouriteProducts;
	}

	/**
	 * <ol>
	 * 	<li>Gets the list of products according to the provided name</li>
	 * 	<li>Checks if the product list is null</li>
	 * 	<li>Checks if the product list is empty</li>
	 * </ol>
	 * 
	 * @param productName the key search
	 * @return the products list that contains the provided name
	 * @throws {@link PharmacyException} with HTTP {@link Response} status 502 (BAD GATEWAY) if some problem happened in database
	 */
	public List<Product> getAllByName(String productName) {
		List<Product> productsFound = productDAO.findAllByName(productName);
		
		if (productsFound == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		return productsFound;
	}

	/**
	 * <ol>
	 * 	<li>Gets all products of this order</li>
	 *  <li>Verifies if this list is null</li>
	 * </ol>
	 * @param orderId primary key that identifies the order that contains the products list 
	 * @return the {@link Product} {@link List} of the given order
	 */
	public List<Product> getAllByOrderId(Short orderId) {
		List<Product> products = productDAO.findAllByOrderId(orderId);
		
		if (products == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		return products;
	}

	/**
	 * <ol>
	 * 	<li>Gets the amount of products existent in database</li>
	 * 	<li>Verifies if that number is null</li>
	 * </ol>
	 * 
	 * @return
	 */
	public Short countAll() {
		Short amountProducts = productDAO.countAll();
		
		if (amountProducts == null) {
			throw new PharmacyException(Response.Status.BAD_GATEWAY, "Database unavailable", "Problems connecting database");
		}
		
		return amountProducts;
	}
}