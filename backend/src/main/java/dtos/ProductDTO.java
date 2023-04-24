package dtos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import enums.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Product information type that the frontend consumes and produces.
 * 
 * @author Wanderley Drumond
 *
 */
@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDTO implements Serializable {
	private Short id, totalLikes/*, amountComments*/;
	private String name, image;
	private Float price;
	private Section section;
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
}