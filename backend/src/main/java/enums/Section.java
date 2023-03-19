package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the different product sections available in the system.
 * 
 * <p>Each section has a value.</p>
 * <ul>
 *   <li>BEAUTY: value = "Beauty"</li>
 *   <li>HEALTH: value = "Health"</li>
 *   <li>SUPPLEMENTS: value = "Supplements"</li>
 * </ul>
 * 
 * @author Wanderley Drumond
 */
@AllArgsConstructor
@Getter
public enum Section {
	BEAUTY ("Beauty"),
	HEALTH ("Health"), 
	SUPPLEMENTS ("Supplements");
	
	private final String VALUE;
}