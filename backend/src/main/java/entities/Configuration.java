package entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Configuration information type that backend consumes and produces.
 * 
 * @author Wanderley Drumond
 */
@Entity
@Table(name = "configurations")
public @Data class Configuration implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Byte  id;
	@Column(name = "total_logins")
	private Short totalLogins;
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;
}