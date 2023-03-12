package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the different user roles available in the system.
 * 
 * <p>Each role has a role code and a role value.</p>
 * <ul>
 *   <li>ADMINISTRATOR: value = "administrator"</li>
 *   <li>CLIENT: value = "client"</li>
 *   <li>VISITOR: value = "visitor"</li>
 * </ul>
 * 
 * @author Wanderley Drumond
 */
@AllArgsConstructor
@Getter
public enum Role {
	ADMINISTRATOR ("administrator"),
	CLIENT("client"), 
	VISITOR("visitor");
	
	private final String VALUE;
}