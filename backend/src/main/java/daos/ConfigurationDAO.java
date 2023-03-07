package daos;

import entities.Configuration;

public class ConfigurationDAO extends GenericDAO<Configuration> {
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;

	public ConfigurationDAO() {
		super(Configuration.class);
	}
}
