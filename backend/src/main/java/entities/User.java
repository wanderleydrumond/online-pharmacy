package entities;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * User information type that backend consumes and produces.
 * 
 * @author Wanderley Drumond
 */
@Entity
@NoArgsConstructor
/**
 * Custom constructor used only in {@link UserMapper} </p>.
 * 
 * @param name
 * @param username
 * @param password
 * @param role
 * 
 * @see	  <a href= "https://github.com/projectlombok/lombok/issues/1269">Implicit @RequiredArgsConstructor on @Data will be removed when using @NoArgsConstructor #1269</a>
 */
@RequiredArgsConstructor
@Table(name = "users")
public @Data class User implements Serializable {
	/**
	 * User identification in database.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;
	/**
	 * The name of the user.
	 */
	@NotBlank
	private @NonNull String name;
	/**
	 * Indicates if an user is logged or not.
	 * 
	 * @see <a href= "https://www.codementor.io/@petrepopescu/how-to-use-string-uuid-in-hibernate-with-mysql-1jrhjh6ef5">How to use String UUID in Hibernate with MySQL</a> 
	 */
	@Column(columnDefinition = "VARCHAR(36)")
	@Type(type = "uuid-char")
	private UUID token;
	/**
	 * Identification used by user to sign into the system.
	 */
	@NotBlank
	private @NonNull String username;
	/**
	 * User's password used to validate them along with the username.
	 */
	@NotBlank
	private @NonNull String password;
	/**
	 * Used to determine the permissions the an user has in the system.
	 */
	@Enumerated(EnumType.STRING)
	private @NonNull Role role;
	/**
	 * The user cannot be hard deleted from the database. This indicates if they are active or not in the system.
	 */
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	
	/**
	 * All the concluded orders made by the user. 
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY)
	private List<Order> orders;
	/**
	 * All the comments made by the user.
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private List<Comment> comments;
	/**
	 * All products that the user marked as like.
	 */
	@JsonIgnore
	@ManyToMany(mappedBy = "usersThatLiked", fetch = FetchType.LAZY)
	private List<Product> likedProducts;
	/**
	 * All products that the user marked as favourite.
	 */
	@JsonIgnore
	@ManyToMany(mappedBy = "usersThatFavorited", fetch = FetchType.LAZY)
	private List<Product> favoriteProducts;
	
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
}