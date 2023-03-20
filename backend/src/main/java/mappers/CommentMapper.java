package mappers;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import dtos.CommentDTO;
import entities.Comment;
import entities.User;
import services.UserService;

/**
 * Class responsible by transform {@link Comment} data that transits between backend and frontend.
 * 
 * @author Wanderley Drumond
 */
@Stateless
public class CommentMapper {
	@Inject
	UserService userService;
	/**
	 * Changes a {@link CommentDTO} object into a {@link Comment} object.
	 * 
	 * @param commentDTO the object that will be transformed into Entity object
	 * @return the Entity resultant object
	 */
	public Comment toEntity(CommentDTO commentDTO) {
		return new Comment(commentDTO.getContent());
	}
	/**
	 * Changes a {@link Comment} object into a {@link CommentDTO} object.
	 * 
	 * @param comment the object that will be transformed into DTO object
	 * @return the DTO resultant object
	 */
	public CommentDTO toDTO(Comment comment, User user) {
		return new CommentDTO(comment.getId(), user.getId(), comment.getContent(), user.getName(), user.getToken());
	}
	
	/**
	 * Changes a {@link CommentDTO} object list into a {@link Comment} objects list.
	 * 
	 * @param commentsDTO the list that will be transformed into Entity list
	 * @return the {@link Comment} resultant objects list
	 */
	public List<Comment> toEntities(List<CommentDTO> commentsDTO) {
		return commentsDTO.stream().map(this::toEntity).collect(Collectors.toList());
	}
}