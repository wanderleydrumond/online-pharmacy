package mappers;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import dtos.UserDTO;
import entities.User;

/**
 * Class responsible by transform {@link User} data that transits between backend and frontend.
 * 
 * @author Wanderley Drumond
 */
@Stateless
public class UserMapper {
	/**
	 * Changes a {@link UserDTO} object into a {@link User} object.
	 * 
	 * @param userDTO the object that will be transformed into Entity object
	 * @return the {@link User} resultant object
	 */
	public User toEntity(UserDTO userDTO) {
		User user = new User(userDTO.getName(), userDTO.getUsername(), userDTO.getPassword(), userDTO.getRole());
		
		if (userDTO.getUuid() != null) {
			user.setUuid(userDTO.getUuid());
		}
		
		return user;
	}
	
	/**
	 * Changes a {@link User} object into a {@link UserDTO} object.
	 * 
	 * @param user the object that will be transformed into DTO object
	 * @return the {@link UserDTO} resultant object
	 */
	public UserDTO toDTO(User user) {
		UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getUsername(), user.getPassword(), user.getRole());
		
		if (user.getUuid() != null) {
			userDTO.setUuid(user.getUuid());
		}
		
		return userDTO;
	}
	
	/**
	 * Changes a {@link UserDTO} object list into a {@link User} objects list.
	 * 
	 * @param usersDTO the list that will be transformed into Entity list
	 * @return the {@link User} resultant objects list
	 */
	public List<User> toEntities(List<UserDTO> usersDTO) {
		return usersDTO.stream().map(this::toEntity).collect(Collectors.toList());
	}
	
	/**
	 * Changes a {@link User} objects list into a {@link UserDTO} objects list.
	 * 
	 * @param users the list that will be transformed into DTO list
	 * @return the {@link UserDTO} resultant objects list
	 */
	public List<UserDTO> toDTOs(List<User> users) {
		return users.stream().map(this::toDTO).collect(Collectors.toList());
	}
}