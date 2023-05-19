package controllers;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import dtos.ProductDTO;
import entities.Product;
import exceptions.PharmacyException;
import mappers.ProductMapper;
import services.ProductService;

/**
 * Class that contains all requisition methods that refers to product.
 * 
 * @author Wanderley Drumond
 */
@Path("/product")
public class ProductController {
	/**
	 * Object that contains all product service methods.
	 */
	@Inject
	private ProductService productService;
	
	/**
	 * Object that contains method that allows to switch between {@link Product} and {@link ProductDTO}.
	 */
	@Inject
	private ProductMapper productMapper;
	
	/**
	 * Creates a new product by an administrator.
	 * 
	 * @param token		  logged administrator user identifier key
	 * @param requestBody the request content
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the product was successfully created in the database</li>
	 *         <li><strong>401 (UNAUTHORISED)</strong> if the logged user is not an administrator</li>
	 *      </ul>
	 */
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@HeaderParam("token") UUID token, ProductDTO requestBody) {
		try {
			Product product = productService.create(token, requestBody);
			ProductDTO productDTO = productMapper.toDTO(product);
			
			return Response.ok(productDTO).build();
		} catch (PharmacyException pharmacyException) {
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * Gets all the products from the provided section checking if those products were liked and/or marked as favourite.
	 * 
	 * @param token					 logged user identifier key
	 * @param section				 which the list of products belongs
	 * @param verifyLikedOrFavorited it will check if this product was liked and/or marked as favourite?
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the products list was found and has elements</li>
	 *         <li><strong>204 (NO CONTENT)</strong> if the product list was found and is empty</li>
	 *         <li><strong>404 (NOT FOUND)</strong> if the provided enumerator value does not exists in database</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/all-by")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllBySection(@HeaderParam("token") UUID token, @QueryParam("section") String section, @QueryParam("verify") boolean verifyLikedOrFavorited) {
		List<Product> products = productService.getAllBySection(section);
		List<ProductDTO> productsDTO = productMapper.toDTOs(products, verifyLikedOrFavorited, token);
		try {
			
			return productsDTO.isEmpty() ? Response.status(Response.Status.NO_CONTENT).entity(productsDTO).build() : Response.ok(productsDTO).build();
		} catch (PharmacyException pharmacyException) {

			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * Gets all product sections.
	 * 
	 * @return {@link Response} with status code <strong>200 (OK)</strong> if the section list was found and has elements
	 */
	@Path("/all-sections")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSections() {
		return Response.ok(productService.getAllSections()).build();
	}
	
	/**
	 * Gets all products.
	 * 
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the product list was found and has elements</li>
	 *         <li><strong>204 (NO CONTENT)</strong> if the product list was found and is empty</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@HeaderParam("token") UUID token, @QueryParam("verify") boolean verifyLikedOrFavorited) {
		try {
			List<Product> products = productService.getAll();
			List<ProductDTO> productsDTO = productMapper.toDTOs(products, verifyLikedOrFavorited, token);
			
			return productsDTO.isEmpty() ? Response.status(Response.Status.NO_CONTENT).entity(productsDTO).build() : Response.ok(productsDTO).build();
			
		} catch (PharmacyException pharmacyException) {
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		} 
	}
	
	/**
	 * Gets the product data by its id.
	 * 
	 * @param productId primary key that identifies the product to be found
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the product was found along with the {@link ProductDTO}</li>
	 *         <li><strong>406 (NOT ACCEPTABLE)</strong> if the product id type is different then {@link Short}, {@link Integer} or {@link Byte}</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/by")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@QueryParam("id") String productId) {
		try {
			return Response.ok(productMapper.toDTO(productService.getById(Short.parseShort(productId)))).build();
		} catch (NumberFormatException numberFormatException) {
			System.err.println("Catch " + numberFormatException.getClass().getName() + " in getById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Incorrect number format for id", numberFormatException);
			numberFormatException.printStackTrace();
			
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Incorrect number format for id").build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in getById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Database unavailable", pharmacyException);
			
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * The logged user likes a product.
	 * 
	 * @param token		logged user identifier key
	 * @param productId primary key that identifies the product to like
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user liked the product along with true {@link boolean} value</li>
	 *         <li><strong>406 (NOT ACCEPTABLE)</strong> if the product id type is different then {@link Short}, {@link Integer} or {@link Byte}</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/like")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response likeById(@HeaderParam("token") UUID token, @QueryParam("id") String productId) {
		try {
			return Response.ok(productService.likeById(token, Short.valueOf(productId))).build();
		} catch (NumberFormatException numberFormatException) {
			System.err.println("Catch " + numberFormatException.getClass().getName() + " in likeById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Incorrect number format for id", numberFormatException);
			numberFormatException.printStackTrace();
			
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Incorrect number format for id").build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in likeById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Database unavailable", pharmacyException);
			
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * The logged user unlike a product.
	 * 
	 * @param token		logged user identifier key
	 * @param productId primary key that identifies the product to unlike
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user unliked the product along with true {@link boolean} value</li>
	 *         <li><strong>406 (NOT ACCEPTABLE)</strong> if the product id type is different then {@link Short}, {@link Integer} or {@link Byte}</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/unlike")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response unlikeById(@HeaderParam("token") UUID token, @QueryParam("id") String productId) {
		try {
			return Response.ok(productService.unlikeById(token, Short.valueOf(productId))).build();
		} catch (NumberFormatException numberFormatException) {
			System.err.println("Catch " + numberFormatException.getClass().getName() + " in unlikeById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Incorrect number format for id", numberFormatException);
			numberFormatException.printStackTrace();
			
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Incorrect number format for id").build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in unlikeById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Database unavailable", pharmacyException);
			
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * The logged user marks a product as favourite.
	 * 
	 * @param token		logged user identifier key
	 * @param productId primary key that identifies the product to favourite
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user marks the product as favourite along with true {@link boolean} value</li>
	 *         <li><strong>406 (NOT ACCEPTABLE)</strong> if the product id type is different then {@link Short}, {@link Integer} or {@link Byte}</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/favourite")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response favoriteById(@HeaderParam("token") UUID token, @QueryParam("id") String productId) {
		try {
			return Response.ok(productService.favoriteById(token, Short.valueOf(productId))).build();
		} catch (NumberFormatException numberFormatException) {
			System.err.println("Catch " + numberFormatException.getClass().getName() + " in favoriteById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Incorrect number format for id", numberFormatException);
			numberFormatException.printStackTrace();
			
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Incorrect number format for id").build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in favoriteById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Database unavailable", pharmacyException);
			
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * The logged user marks off a product as favourite.
	 * 
	 * @param token		logged user identifier key
	 * @param productId primary key that identifies the product to unfavourite
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user marks off the product as favourite along with true {@link boolean} value</li>
	 *         <li><strong>406 (NOT ACCEPTABLE)</strong> if the product id type is different then {@link Short}, {@link Integer} or {@link Byte}</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/unfavourite")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response unfavoriteById(@HeaderParam("token") UUID token, @QueryParam("id") String productId) {
		try {
			return Response.ok(productService.unfavoriteById(token, Short.valueOf(productId))).build();
		} catch (NumberFormatException numberFormatException) {
			System.err.println("Catch " + numberFormatException.getClass().getName() + " in unfavoriteById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Incorrect number format for id", numberFormatException);
			numberFormatException.printStackTrace();
			
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Incorrect number format for id").build();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in unfavoriteById() in ProductController");
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "Database unavailable", pharmacyException);
			
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * Gets all favourite products from the logged user.
	 * 
	 * @param token	logged user identifier key
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the user was able to find their favourite products along with {@link ProductDTO} list</li>
	 *         <li><strong>401 (UNAUTHORISED)</strong> if user is not logged</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/favourites")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllFavoritesByToken(@HeaderParam("token") UUID token, @QueryParam("verify") boolean verifyLikedOrFavorited) {
		try {
			List<Product> favorites = productService.getAllFavoritesByToken(token);
			List<ProductDTO> favoritesDTO = productMapper.toDTOs(favorites, verifyLikedOrFavorited, token);
			
			return Response.ok(favoritesDTO).build();
		} catch (PharmacyException pharmacyException) {
			Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, "in getAllByToken() in ProductController", pharmacyException);
			
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * Gets all the products that contains the provided name checking if those products were liked and/or marked as favourite.
	 * 
	 * @param token					 logged user identifier key
	 * @param productName			 the key search
	 * @param verifyLikedOrFavorited it will check if this product was liked and/or marked as favourite?
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the products list was found and has elements</li>
	 *         <li><strong>204 (NO CONTENT)</strong> if the products list was found and is empty</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/all-by-")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllByName(@HeaderParam("token") UUID token, @QueryParam("name") String productName, @QueryParam("verify") boolean verifyLikedOrFavorited) {
		List<Product> productsFound = productService.getAllByName(productName);
		List<ProductDTO> productsDTOFound = productMapper.toDTOs(productsFound, verifyLikedOrFavorited, token);
		
		return productsDTOFound.isEmpty() ? Response.status(Response.Status.NO_CONTENT).entity(productsDTOFound).build() : Response.ok(productsDTOFound).build();
	}
}