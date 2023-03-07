package enums;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the different roles available in the system.
 * 
 * <p>Each role has a role code and a role value.</p>
 * <ul>
 *   <li>ADMINISTRATOR:  code = 1, value = "administrator"</li>
 *   <li>CLIENT: code = 2, value = "client"</li>
 *   <li>VISITOR: code = 3, value = "visitor"</li>
 * </ul>
 * 
 * @JsonFormat Configures the shape of JSON objects representing this enumeration, using Shape.OBJECT it is represented with the fields of the enumeration as JSON objects.
 * 
 * <p>Provides a set of methods to convert between the {@link Role} enumeration values and their corresponding database representation.</p>
 * <p>Also, provides a mechanism to deserialise a role value from JSON using Jackson annotations.</p>
 */
@AllArgsConstructor
@Getter
@JsonFormat(shape = Shape.OBJECT)
public enum Role {
	ADMINISTRATOR ((byte) 1, "administrator"),
	CLIENT ((byte) 2, "client"), 
	VISITOR ((byte) 3, "visitor");
	
	private final Byte roleCode;
	private final String roleValue;
	private static Map<Byte, Role> roleCodestoValuesMapping = new LinkedHashMap<>();
	
	static {
		Arrays.stream(Role.values()).forEach(role -> roleCodestoValuesMapping.put(role.getRoleCode(), role));
	}
	
	/**
	 * Converts a {@link Role} object into its corresponding database representation, which is a Byte object.
	 *
	 * @param role the Role object to be converted.
	 * @return the Byte representation of the roleCode field of the Role object.
	 */
	public static Byte toDatabaseColumn(Role role) {
		return role.roleCode;
	}
	
	/**
	 * Returns a Role object given a code.
	 *
	 * @param code the code of the role as Byte.
	 * @return the Role object mapped to the code or null if the code is not valid.
	 * @JsonCreator Indicates that this method is to be used to create an instance of Role when reading JSON.
	 */
	@JsonCreator(mode = Mode.DELEGATING)
	public static Role toEntityAttribute(Byte code) {
		return roleCodestoValuesMapping.get(code);
	}
}