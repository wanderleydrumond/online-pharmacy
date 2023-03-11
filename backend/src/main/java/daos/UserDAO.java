package daos;

import java.util.Optional;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.ws.rs.core.Response;

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
			exception.printStackTrace();
			
			return Optional.empty();
		}
	}

	public Optional<User> findByUUID(UUID token) {
		try {
			final CriteriaQuery<User> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(User.class);
			Root<User> userTable = CRITERIA_QUERY.from(User.class);
			
			CRITERIA_QUERY.select(userTable).where(criteriaBuilder.and(
					criteriaBuilder.equal(userTable.get("token"), token)));
			
			return Optional.ofNullable(entityManager.createQuery(CRITERIA_QUERY).getSingleResult());
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in findByUUID() in UserDAO");
			exception.printStackTrace();
			
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
			pharmacyException.printStackTrace();
			
			throw new PharmacyException(Response.Status.SERVICE_UNAVAILABLE, "Error", "Error in database has occured");
		}
	}

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
			
			return false;
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in exists() in UserDAO");
			exception.printStackTrace();
			
			return null;
		}
	}

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
			System.err.println("Catch " + pharmacyException.getClass().getName() + " in signOut() in UserDAO");
			pharmacyException.printStackTrace();
			
			throw new PharmacyException(Response.Status.SERVICE_UNAVAILABLE, "Error", "Error in database has occured");
		}
	}
}