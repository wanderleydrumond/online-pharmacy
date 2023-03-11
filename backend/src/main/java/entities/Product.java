package entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Product information type that backend consumes and produces.
 * 
 * @author Wanderley Drumond
 */
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "products")
public @Data class Product implements Serializable {
	/**
	 * Product identification in database.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;
	/**
	 * Product's name.
	 */
	@NotBlank
	private @NonNull String name;
	/**
	 * Product's price.
	 */
	@NotBlank
	private @NonNull Float price;
	/**
	 * Which group this product belongs.
	 */
	@NotBlank
	private @NonNull String section;
	/**
	 * Product image URI.
	 */
	@NotBlank
	private @NonNull String image;
	
	/**
	 * All the comments that this products received from the users.
	 */
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Comment> comments;
	/**
	 * All users that liked this product.
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "liked_products", joinColumns = @JoinColumn(name = "liked_product_id"), inverseJoinColumns = @JoinColumn(name = "user_that_liked_id"))
	private List<User> 	  usersThatLiked;
	/**
	 * All users that marked this product as favourite.
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "favorite_products", joinColumns = @JoinColumn(name = "favorite_product_id"), inverseJoinColumns = @JoinColumn(name = "user_that_favorited_id"))
	private List<User> 	  usersThatFavorited;
	/**
	 * All orders from this product (concluded and on going).
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "ordered_products", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "order_id"))
	private List<Order>   ordersOfAProduct;
	
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
}