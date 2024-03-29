package daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import entities.Order;
import entities.Product;
import entities.User;
import enums.Section;

/**
 * Class that makes the database communication layer role in relation with of the products table.
 * 
 * @author Wanderley Drumond
 *
 */
@Stateless
public class ProductDAO extends GenericDAO<Product> {

	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	public ProductDAO() {
		super(Product.class);
	}

	/**
	 * Finds in the database the list of products that belongs to the provided section.
	 * 
	 * @param section which the list of products belongs
	 * @return
	 * 		<ul>If:
	 * 			<li>the {@link List} of the products that belongs to the provided section</li>
	 * 			<li>any errors happened in the database, null</li>
	 * 		</ul>
	 */
	public List<Product> findAllBySection(Section section) {
		try {
			final CriteriaQuery<Product> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Product.class);
			Root<Product> productTable = CRITERIA_QUERY.from(Product.class);
			
			CRITERIA_QUERY.select(productTable).where(criteriaBuilder.equal(productTable.get("section"), section));
			
			return entityManager.createQuery(CRITERIA_QUERY).getResultList();
		} catch (Exception exception) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "in findAllBySection() in ProductDAO", exception);
			
			return null;
		}
	}

	/**
	 * Finds the {@link Optional} {@link Product} by its id.
	 * 
	 * @param productId primary key that identifies the product to be found
	 * @return the {@link Optional} {@link Product} that owns the provided id
	 */
	public Optional<Product> findById(Short productId) {
		try {
			final CriteriaQuery<Product> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Product.class);
			Root<Product> productTable = CRITERIA_QUERY.from(Product.class);
			
			CRITERIA_QUERY.select(productTable).where(criteriaBuilder.equal(productTable.get("id"), productId));
			
			return Optional.ofNullable(entityManager.createQuery(CRITERIA_QUERY).getSingleResult());
		} catch (NoResultException noResultException) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.FINE, "in findById() in ProductDAO", noResultException);
			
			return Optional.empty();
		} catch (Exception exception) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "in findById() in ProductDAO", exception);			
			
			return null;
		}
	}

	/**
	 * Finds all favourite products of the logged user.
	 * 
	 * @param token logged user identifier key
	 * @return
	 * 		<ul>If:
	 * 			<li>the {@link List} of favourite products of the logged user</li>
	 * 			<li>any errors happened in the database, null</li>
	 * 		</ul>
	 */
	public List<Product> findAllfavoritesByToken(UUID token) {
		try {
			final CriteriaQuery<Product> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Product.class);
			Root<Product> productTable = CRITERIA_QUERY.from(Product.class);
			Join<Product, User> userTable = productTable.join("usersThatFavorited");
			
			CRITERIA_QUERY.select(productTable).where(criteriaBuilder.equal(userTable.get("token"), token));
			
			return entityManager.createQuery(CRITERIA_QUERY).getResultList();
		} catch (Exception exception) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "in findAllfavoritesByToken() in ProductDAO", exception);
			
			return null;
		}
	}

	/**
	 * Finds the list of products that contains the provided key search in their product names
	 * 
	 * @param keysearch the product name or part of it
	 * @return
	 * 		<ul>If:
	 * 			<li>the {@link List} of the products that contains the provided key search in their names</li>
	 * 			<li>any errors happened in the database, null</li>
	 * 		</ul>
	 */
	public List<Product> findAllByName(String keysearch) {
		try {
			final CriteriaQuery<Product> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Product.class);
			Root<Product> productTable = CRITERIA_QUERY.from(Product.class);
			
			CRITERIA_QUERY.select(productTable).where(criteriaBuilder.or(
					criteriaBuilder.like(productTable.get("name"), keysearch + '%'),
					criteriaBuilder.like(productTable.get("name"), '%' + keysearch + '%'),
					criteriaBuilder.like(productTable.get("name"), '%' + keysearch)));
			
			return entityManager.createQuery(CRITERIA_QUERY).getResultList();
		} catch (Exception exception) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "in findAllByName() in ProductDAO", exception);
			
			return null;
		}
	}

	/**
	 * Finds all products that belongs to the given order id.
	 * 
	 * @param orderId that contains the products list to be found
	 * @return the {@link Product} {@link List} of the products that belogs to the given orderId
	 */
	public List<Product> findAllByOrderId(Short orderId) {
		try {
			final CriteriaQuery<Product> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Product.class);
			Root<Product> productTable = CRITERIA_QUERY.from(Product.class);
			Join<Product, Order> orderTable = productTable.join("ordersOfAProduct");
			
			CRITERIA_QUERY.select(productTable).where(criteriaBuilder.equal(orderTable.get("id"), orderId));
			
			return entityManager.createQuery(CRITERIA_QUERY).getResultList();
		} catch (NoResultException noResultException) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.FINE, "in findAllThatFavouritedThisProduct()", noResultException);
			
			return new ArrayList<Product>();
		} catch (Exception exception) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "in findAllByOrderId() in ProductDAO", exception);
			
			return null;
		}
	}
	
	/**
	 * Verifies if the logged user liked the provided product.
	 * 
	 * @param token	  logged user identifier key
	 * @param product object to the checked if it was liked
	 * @return If: 
	 * 	<ul>
	 * 		<li>Yes, true</li>
	 * 		<li>Not, false</li>
	 * 	</ul>
	 */
	public Boolean verifyLoggedUserLiked(UUID token, Product product) {
		try {
			final CriteriaQuery<User> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(User.class);
			Root<User> userTable = CRITERIA_QUERY.from(User.class);
			Join<User, Product> productsLiked = userTable.join("likedProducts");
			
			CRITERIA_QUERY.select(userTable).where(criteriaBuilder.and(
					criteriaBuilder.equal(userTable.get("token"), token),
					criteriaBuilder.equal(productsLiked.get("id"), product.getId())
					));
			
			entityManager.createQuery(CRITERIA_QUERY).getSingleResult();
			
			return true;
		} catch (NoResultException noResultException) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.FINE, "in verifyLoggedUserFavorited() in ProductDAO", noResultException);
			
			return false;
		} catch (Exception exception) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "in verifyLoggedUserLiked() in ProductDAO", exception);
			
			return null;
		}
	}

	/**
	 * Verifies if the logged user marked the provided product as favourite.
	 * 
	 * @param token	  logged user identifier key
	 * @param product object to the checked if it was marked as favourite
	 * @return If: 
	 * 	<ul>
	 * 		<li>Yes, true</li>
	 * 		<li>Not, false</li>
	 * 	</ul>
	 */
	public Boolean verifyLoggedUserFavorited(UUID token, Product product) {
		try {
			final CriteriaQuery<User> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(User.class);
			Root<User> userTable = CRITERIA_QUERY.from(User.class);
			Join<User, Product> productsFavorited = userTable.join("favoriteProducts");
			
			CRITERIA_QUERY.select(userTable).where(criteriaBuilder.and(
					criteriaBuilder.equal(userTable.get("token"), token),
					criteriaBuilder.equal(productsFavorited.get("id"), product.getId())
					));
			
			entityManager.createQuery(CRITERIA_QUERY).getSingleResult();
			
			return true;
		} catch (NoResultException noResultException) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.FINE, "in verifyLoggedUserFavorited() in ProductDAO", noResultException);
			
			return false;
		} catch (Exception exception) {
			Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, "in verifyLoggedUserFavorited() in ProductDAO", exception);
			
			return null;
		}
	}
}