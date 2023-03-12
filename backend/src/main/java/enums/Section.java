package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the different product sections available in the system.
 * 
 * <p>Each role has a role code and a role value.</p>
 * <ul>
 *   <li>BEAUTY: value = "beauty"</li>
 *   <li>HEALTH: value = "health"</li>
 *   <li>SUPPLEMENTS: value = "supplements"</li>
 * </ul>
 * 
 * @author Wanderley Drumond
 */
@AllArgsConstructor
@Getter
public enum Section {
	BEAUTY ("beauty"),
	HEALTH ("health"), 
	SUPPLEMENTS ("supplements");
	
	private final String VALUE;
}