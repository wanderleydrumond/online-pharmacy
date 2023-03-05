package entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Product information type that backend consumes and produces.
 * 
 * @author Wanderley Drumond
 */
@Entity
@Table(name = "products")
public @Data class Product implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Byte 	id;
	@NotBlank
	private String 	name;
	@NotBlank
	private Double 	price;
	@NotBlank
	private String 	group;
	@NotBlank
	private String 	image;
	
	@OneToMany(fetch = FetchType.LAZY)
	private List<Comment> comments;
	@ManyToMany(fetch = FetchType.LAZY)
	private List<User> 	  usersThatLiked;
	@ManyToMany(fetch = FetchType.LAZY)
	private List<User> 	  usersThatFavorited;
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Order>   ordersOfAProduct;
	
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;
}