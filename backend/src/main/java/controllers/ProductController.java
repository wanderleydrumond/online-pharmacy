package controllers;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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
	
	/**
	 * Gets all the products from the provided section.
	 * 
	 * @param section which the list of products belongs
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the products list was found and has elements</li>
	 *         <li><strong>202 (NO CONTENT)</strong> if the products list was found and is empty</li>
	 *         <li><strong>404 (NOT FOUND)</strong> if the provided enumerator value does not exists in database</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/all-by")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllBySection(@QueryParam("section") String section) {
		List<Product> products = productService.getAllBySection(section);
		try {
			return Response.ok(products).build();
		} catch (PharmacyException pharmacyException) {
			// FIXME I cannot get the proper header value.

			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
	
	/**
	 * Gets all product sections.
	 * 
	 * @return {@link Response} with status code:
	 *      <ul>
	 *         <li><strong>200 (OK)</strong> if the section list was found and has elements</li>
	 *         <li><strong>202 (NO CONTENT)</strong> if the section list was found and is empty</li>
	 *         <li><strong>502 (BAD GATEWAY)</strong> if some problem happened in database</li>
	 *      </ul>
	 */
	@Path("/all-sections")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSections() {
		try {
			return Response.ok(productService.getAllSections()).build();
		} catch (PharmacyException pharmacyException) {
			return Response.status(pharmacyException.getHttpStatus()).header("Impossible to proceed", pharmacyException.getHeader()).entity(pharmacyException.getMessage()).build();
		}
	}
}