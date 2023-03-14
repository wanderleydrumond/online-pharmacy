package controllers;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
	ProductService productService;
	
	/**
	 * Object that contains method that allows to switch between {@link Product} and {@link ProductDTO}.
	 */
	@Inject
	ProductMapper productMapper;
	
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
}