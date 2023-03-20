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
	/**
	 * Comment identification in database.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;
	/**
	 * The text of the comment.
	 */
	private @NonNull String content;
	
	/**
	 * Who made the comment.
	 */
	@ManyToOne
	private User owner;
	/**
	 * Product about the comment is.
	 */
	@ManyToOne
	private Product product;
	
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
}