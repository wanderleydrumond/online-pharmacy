package entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Order information type that backend consumes and produces.
 * 
 * @author Wanderley Drumond
 */
@Entity
@NoArgsConstructor
/**
 * Custom constructor used only in {@link OrderMapper}</p>.
 * @param totalValue single parameter necessary to pass on the information to frontend
 * @see	  <a href= "https://github.com/projectlombok/lombok/issues/1269">Implicit @RequiredArgsConstructor on @Data will be removed when using @NoArgsConstructor #1269</a>
 */
@RequiredArgsConstructor 
@Table(name = "orders")
public @Data class Order implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Byte id;
	@UpdateTimestamp
	@Column(name = "last_update")
	private Timestamp lastUpdate;
	@CreationTimestamp
	@Column(name = "finished_in")
	private Timestamp finishedIn;
	@Column(name = "total_value")
	private @NonNull Float totalValue;
	@Column(name = "is_concluded")
	private Boolean isConcluded;
	
	@ManyToOne
	private User buyer;
	@ManyToMany(mappedBy = "ordersOfAProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Product> productsOfAnOrder;
	
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;
}
