package mappers;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import enums.Role;

/**
 * Converts between the {@link Role} enum and its corresponding database representation as a Byte.
 * 
 * @author Wanderley Drumond
 */
@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Byte> {
	/**
	 *{@inheritDoc}
	 */
	@Override
	public Byte convertToDatabaseColumn(Role role) {
		return Role.toDatabaseColumn(role);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public Role convertToEntityAttribute(Byte code) {
		return Role.toEntityAttribute(code);
	}
}