package daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.ws.rs.core.Response;

import entities.Product;
import entities.User;
import enums.Role;
import exceptions.PharmacyException;

/**
 * Class that makes the database communication layer role in relation with of the users table.
 * 
 * @author Wanderley Drumond
 *
 */
@Stateless
public class UserDAO extends GenericDAO<User> {
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	public UserDAO() {
		super(User.class);
	}

	/**
	 * Finds a user that have the given username and password.
	 * 
	 * @param username
	 * @param password
	 * @return
	 * 		<ul>
	 * 			<li>The {@link Optional} {@link User} who own the match of the given parameters</li>
	 * 			<li>{@link Optional} empty if no one was found with the given match</li>
	 * 		</ul>
	 */
	public Optional<User> signIn(String username, String password) {
		try {
			final CriteriaQuery<User> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(User.class);
			Root<User> userTable = CRITERIA_QUERY.from(User.class);
			
			CRITERIA_QUERY.select(userTable).where(criteriaBuilder.and(
					criteriaBuilder.equal(userTable.get("username"), username), 
					criteriaBuilder.equal(userTable.get("password"), password)));
			
			return Optional.ofNullable(entityManager.createQuery(CRITERIA_QUERY).getSingleResult());
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in signIn() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in signIn()", exception);
			// exception.printStackTrace();
			
			return Optional.empty();
		}
	}

	/**
	 * Finds an user by their UUID.
	 * 
	 * @param token logged user identifier key
	 * @return If:
	 * 		<ul>
	 * 			<li>Finds, {@link Optional} {@link User} corresponding </li>
	 * 			<li>Does not find, {@link Optional} empty</li>
	 * 		</ul>
	 */
	public Optional<User> findByUUID(UUID token) {
		try {
			final CriteriaQuery<User> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(User.class);
			Root<User> userTable = CRITERIA_QUERY.from(User.class);
			
			CRITERIA_QUERY.select(userTable).where(criteriaBuilder.and(
					criteriaBuilder.equal(userTable.get("token"), token),
					criteriaBuilder.notEqual(userTable.get("role"), Role.VISITOR)));
			
			return Optional.ofNullable(entityManager.createQuery(CRITERIA_QUERY).getSingleResult());
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in findByUUID() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in findByUUID()", exception);
			// exception.printStackTrace();
			
			return Optional.empty();
		}
	}

	/**
	 * Updates the users table removing the given token from its owner.
	 * 
	 * @param token logged user identifier key
	 * @return the amount of rows updated
	 * @throws {@link PharmacyException} with 503 status code (SERVICE UNAVAILABLE) if any errors occurs with database
	 */
	public Integer signOut(UUID token) {
		try {
			final CriteriaUpdate<User> CRITERIA_UPDATE;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_UPDATE = criteriaBuilder.createCriteriaUpdate(User.class);
			Root<User> userTable = CRITERIA_UPDATE.from(User.class);
			
			CRITERIA_UPDATE.set("token", null);
			CRITERIA_UPDATE.where(criteriaBuilder.equal(userTable.get("token"), token));
			
			return entityManager.createQuery(CRITERIA_UPDATE).executeUpdate();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in signOut() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in signOut()", pharmacyException);
			//pharmacyException.printStackTrace();
			
			throw new PharmacyException(Response.Status.SERVICE_UNAVAILABLE, "Error", "Error in database has occured");
		}
	}

	/**
	 * Checks if the user that holds the given token has the role of ADMINISTRATOR.
	 * 
	 * @param token logged user identifier key
	 * @return
	 * 		<ul>
	 * 			<li>True, if the provided token belongs to an active administrator</li>
	 * 			<li>False, if the provided token does not belong to an active administrator</li>
	 * 			<li>Null, if any errors have occurred in the database.</li>
	 * 		</ul>
	 */
	public Boolean checkIfIsAdmin(UUID token) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

			CriteriaQuery<Boolean> query = criteriaBuilder.createQuery(Boolean.class);
			query.from(User.class);
			query.select(criteriaBuilder.literal(true));

			Subquery<User> subquery = query.subquery(User.class);
			Root<User> subRootEntity = subquery.from(User.class);
			subquery.select(subRootEntity);

			Predicate checkToken = criteriaBuilder.equal(subRootEntity.get("token"), token);
			Predicate checkIsDeleted = criteriaBuilder.equal(subRootEntity.get("isDeleted"), false);
			
			subquery.where(criteriaBuilder.and(checkToken, checkIsDeleted));
			query.where(criteriaBuilder.exists(subquery));

			TypedQuery<Boolean> typedQuery = entityManager.createQuery(query);
			
			return typedQuery.getSingleResult();
		} catch (NoResultException noResultException) {
			System.out.println("Catch " + noResultException.getClass().getName() + " in exists() in UserDAO.");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in exists()", noResultException);

			return false;
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in exists() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in exists()", exception);
			// exception.printStackTrace();
			
			return null;
		}
	}

	/**
	 * <p>Changes the role of the user that owns the given id to VISITOR.</p>
	 * <p><em>The logged user must be an administrator.</em></p>
	 * 
	 * @param userToApproveId primary key that identifies the user to be updated
	 * @return the amount of rows affected
	 * @throws {@link PharmacyException} with status code 503 (SERVICE UNAVAILABLE) if any errors occurs in database
	 */
	public Integer approve(Integer userToApproveId) {
		try {
			final CriteriaUpdate<User> CRITERIA_UPDATE;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_UPDATE = criteriaBuilder.createCriteriaUpdate(User.class);
			Root<User> userTable = CRITERIA_UPDATE.from(User.class);
			
			CRITERIA_UPDATE.set("role", Role.CLIENT);
			CRITERIA_UPDATE.where(criteriaBuilder.and(
					criteriaBuilder.equal(userTable.get("id"), userToApproveId),
					criteriaBuilder.equal(userTable.get("role"), Role.VISITOR)));
			
			return entityManager.createQuery(CRITERIA_UPDATE).executeUpdate();
		} catch (PharmacyException pharmacyException) {
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in approve() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in approve()", pharmacyException);
			// pharmacyException.printStackTrace();
			
			throw new PharmacyException(Response.Status.SERVICE_UNAVAILABLE, "Error", "Error in database has occured");
		}
	}

	
	/**
	 * Finds all users that liked the product which the provided id belongs.
	 * 
	 * @param productId primary key that identifies the product that contains the list of users that likes it
	 * @return
	 * 		<ul>If:
	 * 			<li>Exists, at least, one record, the list, already existent in database, of users that liked this product</li>
	 * 			<li>Do not exists, a new list</li>
	 * 			<li>Some problem happened, null</li>
	 * 		</ul>
	 */
	public List<User> findAllThatLikedThisProduct(Short productId) {
		try {
			final CriteriaQuery<User> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(User.class);
			Root<User> userTable = CRITERIA_QUERY.from(User.class);
			Join<User, Product> productTable = userTable.join("likedProducts");
			
			CRITERIA_QUERY.select(userTable).where(criteriaBuilder.equal(productTable.get("id"), productId));
			
			return entityManager.createQuery(CRITERIA_QUERY).getResultList();
		} catch (NoResultException noResultException) {
			System.err.println("Catch " + noResultException.getClass().getName() + " in findAllThatLikedThisProduct() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in findAllThatLikedThisProduct()", noResultException);
			// noResultException.printStackTrace();
			
			return new ArrayList<User>();
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in findAllThatLikedThisProduct() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in findAllThatLikedThisProduct()", exception);
			// exception.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * Counts all likes the product which the provided id belongs.
	 * 
	 * @param productId primary key that identifies the product that contains the list of users that likes it
	 * @return
	 * 		<ul>If:
	 * 			<li>Exists, at least, one record, the list size</li>
	 * 			<li>Not exists, 0</li>
	 * 			<li>Some problem happened, null</li>
	 * 		</ul>
	 */
	public Short countTotalLikes(Short productId) {
		try {
			final CriteriaQuery<User> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(User.class);
			Root<User> userTable = CRITERIA_QUERY.from(User.class);
			Join<User, Product> productTable = userTable.join("likedProducts");
			
			CRITERIA_QUERY.select(userTable).where(criteriaBuilder.equal(productTable.get("id"), productId));
			
			return (short) entityManager.createQuery(CRITERIA_QUERY).getResultList().size();
		} catch (NoResultException noResultException) {
			System.err.println("Catch " + noResultException.getClass().getName() + " in countTotalLikes() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in countTotalLikes()", noResultException);
			// noResultException.printStackTrace();
			
			return 0;
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in countTotalLikes() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in countTotalLikes()", exception);
			// exception.printStackTrace();
			
			return null;
		}
	}

	/**
	 * Finds all users that favourited the product which the provided id belongs.
	 * 
	 * @param productId primary key that identifies the product that contains the list of users that set it as favourite
	 * @return
	 * 		<ul>If:
	 * 			<li>Exists, at least, one record, the list, already existent in database, of users that favourited this product</li>
	 * 			<li>Not exists, a new list</li>
	 * 			<li>Some problem happened, null</li>
	 * 		</ul>
	 */
	public List<User> findAllThatFavouritedThisProduct(Short productId) {
		try {
			final CriteriaQuery<User> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(User.class);
			Root<User> userTable = CRITERIA_QUERY.from(User.class);
			Join<User, Product> productTable = userTable.join("favoriteProducts");
			
			CRITERIA_QUERY.select(userTable).where(criteriaBuilder.equal(productTable.get("id"), productId));
			
			return entityManager.createQuery(CRITERIA_QUERY).getResultList();
		} catch (NoResultException noResultException) {
			System.err.println("Catch " + noResultException.getClass().getName() + " in findAllThatFavouritedThisProduct() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in findAllThatFavouritedThisProduct()", noResultException);
			// noResultException.printStackTrace();
			
			return new ArrayList<User>();
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in findAllThatFavouritedThisProduct() in UserDAO");
			Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "in findAllThatFavouritedThisProduct()", exception);
			// exception.printStackTrace();
			
			return null;
		}
	}
}