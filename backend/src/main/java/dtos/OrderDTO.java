package dtos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Order information type that the frontend consumes and produces.
 * 
 * @author Wanderley Drumond
 *
 */
@XmlRootElement
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class OrderDTO implements Serializable {
	private @NonNull Short id;
	private @NonNull String lastUpdate, finishedIn;
	private @NonNull Float totalValue;
	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;
}