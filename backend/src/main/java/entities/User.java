package entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import mappers.RoleConverter;

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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Byte id;
	@NotBlank
	private @NonNull String name;
	private String 	token;
	@NotBlank
	private @NonNull String username;
	@NotBlank
	private @NonNull String password;
	@Convert(converter = RoleConverter.class)
	private @NonNull Role role;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	
	@OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Order> orders;
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Comment> comments;
	@ManyToMany(mappedBy = "usersThatLiked", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Product> likedProducts;
	@ManyToMany(mappedBy = "usersThatFavorited", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Product> favoriteProducts;
	
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;
}