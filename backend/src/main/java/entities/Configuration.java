package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Configuration information type that backend consumes and produces.
 * 
 * @author Wanderley Drumond
 */
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "configurations")
public @Data class Configuration implements Serializable {
	/**
	 * <p>Configuration identification in database.</p>
	 * <p>Not required, used just because JPA does not create tables without ids.</p>
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short  id;
	/**
	 * The amount of sign ins made by all users in the the system.
	 */
	@NotBlank
	private @NonNull String keyword;
	@NotBlank
	private @NonNull String value;
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
}