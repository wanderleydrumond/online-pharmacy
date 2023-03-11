package daos;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import entities.Configuration;

@Stateless
public class ConfigurationDAO extends GenericDAO<Configuration> {
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	public ConfigurationDAO() {
		super(Configuration.class);
	}

	public Optional<Configuration> findValueByKeyWord(String keyword) {
		try {
			final CriteriaQuery<Configuration> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Configuration.class);
			Root<Configuration> configurationTable = CRITERIA_QUERY.from(Configuration.class);
			
			CRITERIA_QUERY.select(configurationTable).where(criteriaBuilder.equal(configurationTable.get("keyword"), keyword));
			
			return Optional.ofNullable(entityManager.createQuery(CRITERIA_QUERY).getSingleResult());
		} catch (Exception exception) {
			System.err.println("Catch " + exception.getClass().getName() + " in sumSignIns() in ConfigurationDAO");
			exception.printStackTrace();
			
			return Optional.empty();
		}
	}
}