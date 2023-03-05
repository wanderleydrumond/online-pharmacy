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
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Order information type that backend consumes and produces.
 * 
 * @author Wanderley Drumond
 */
@Entity
@Table(name = "orders")
public @Data class Order implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Byte 	  id;
	@Column(name = "last_update")
	private Timestamp lastUpdate;
	@Column(name = "finished_in")
	private Timestamp finishedIn;
	@NotNull
	@Column(name = "total_value")
	private Float 	  totalValue;
	@Column(name = "is_concluded")
	private Boolean   isConcluded;
	
	@ManyToOne
	private User 		  buyer;
	@ManyToMany(mappedBy = "ordersOfAProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Product> productsOfAnOrder;
	
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization.<p>
	 */
	private static final long serialVersionUID = 1L;
}
