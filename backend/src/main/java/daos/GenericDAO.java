package daos;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

/**
 * Contains all common actions for used for for objects to interact with
 * database.
 * 
 * @param <T> the object type
 */
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public abstract class GenericDAO<T extends Serializable> implements Serializable {

	/**
	 * <p>The serial version identifier for this class.<p>
	 * 
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constant that will receive the class DAO object to be used.
	 */
	private final Class<T> CLAZZ;

	/**
	 * contains all database methods access.
	 */
	@PersistenceContext(unitName = "backend")
	protected EntityManager entityManager;

	/**
	 * The constructor method which works generic type with to be specified
	 * furthermore.
	 * 
	 * @param clazz
	 */
	public GenericDAO(Class<T> clazz) {
		CLAZZ = clazz;
	}

	/**
	 * Finds the given item into the database.
	 * 
	 * @param primaryKey the entity primary key
	 * @return the resultant object entity
	 */
	public Optional<T> find(Object primaryKey) {
		try {
			return Optional.ofNullable(entityManager.find(CLAZZ, primaryKey));
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates the given item into the database.
	 * 
	 * @param entity the object that contains informations to be inserted
	 */
	public void persist(final T entity) {
		try {
			entityManager.persist(entity);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Updates the given item into the database.
	 * 
	 * @param entity the object that contains informations to be updated
	 */
	public void merge(final T entity) {
		try {
			entityManager.merge(entity);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Removes the given item from the database.
	 * 
	 * @param entity the object that contains informations to be deleted
	 */
	public void remove(final T entity) {
		try {
			if (entityManager.contains(entity)) {
				entityManager.remove(entity);
			} else {
				entityManager.remove(entityManager.merge(entity));
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Finds all results from a determined query.
	 * 
	 * @return the result list
	 */
	public List<T> findAll() {
		final CriteriaQuery<T> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(CLAZZ);
		criteriaQuery.select(criteriaQuery.from(CLAZZ));
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

}