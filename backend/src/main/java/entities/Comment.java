package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Comment information type that backend consumes and produces.
 * 
 * @author Wanderley Drumond
 */
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "comments")
public @Data class Comment implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;
	private @NonNull String content;
	
	@ManyToOne
	private User owner;
	@ManyToOne
	private Product product;
	
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;
}