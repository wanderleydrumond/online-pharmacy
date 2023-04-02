package entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import enums.Section;
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
	@NotNull
	private @NonNull Float price;
	/**
	 * Which section this product belongs.
	 */
	@Enumerated(EnumType.STRING)
	private @NonNull Section section;
	/**
	 * Product image URI.
	 */
	@NotBlank
	private @NonNull String image;
	
	/**
	 * All the comments that this products received from the users.
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<Comment> comments;
	/**
	 * All users that liked this product.
	 */
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "liked_products", joinColumns = @JoinColumn(name = "liked_product_id"), inverseJoinColumns = @JoinColumn(name = "user_that_liked_id"))
	private List<User> usersThatLiked;
	/**
	 * All users that marked this product as favourite.
	 */
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "favorite_products", joinColumns = @JoinColumn(name = "favorite_product_id"), inverseJoinColumns = @JoinColumn(name = "user_that_favorited_id"))
	private List<User> usersThatFavorited;
	/**
	 * All orders from this product (concluded and on going).
	 */
	@JsonIgnore
	@ManyToMany(mappedBy = "productsOfAnOrder", fetch = FetchType.LAZY)
	private List<Order> ordersOfAProduct;
	
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
}