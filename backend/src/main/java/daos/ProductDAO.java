package daos;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import entities.Product;
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
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
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
	 * 			<li>at least one record, the List of the products that belongs to the provided section</li>
	 * 			<li>no records, a new {@link ArrayList}</li>
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
		} catch (NoResultException noResultException) {
			System.err.println("Catch " + noResultException.getClass().getName() + " in findAllBySection() in ProductDAO");
			noResultException.printStackTrace();
			
			return new ArrayList<Product>();
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in findAllBySection() in ProductDAO");
			exception.printStackTrace();
			
			return null;
		}
	}

	/**
	 * Finds in the database the list of product sections.
	 * 
	 * @return
	 * 		<ul>If:
	 * 			<li>at least one record, the List of the products that belongs to the provided section</li>
	 * 			<li>no records, a new {@link ArrayList}</li>
	 * 			<li>any errors happened in the database, null</li>
	 * 		</ul>
	 */
	public List<Section> findAllSections() {
		try {
			final CriteriaQuery<Section> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Section.class);
			Root<Product> productTable = CRITERIA_QUERY.from(Product.class);
			
			CRITERIA_QUERY.select(productTable.get("section"));
			
			return entityManager.createQuery(CRITERIA_QUERY).getResultList();
		} catch (NoResultException noResultException) {
			System.err.println("Catch " + noResultException.getClass().getName() + " in findAllSections() in ProductDAO");
			noResultException.printStackTrace();
			
			return new ArrayList<Section>();
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in findAllBySection() in ProductDAO");
			exception.printStackTrace();
			
			return null;
		}
	}

}
