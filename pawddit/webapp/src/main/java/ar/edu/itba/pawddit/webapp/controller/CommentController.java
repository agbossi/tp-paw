package ar.edu.itba.pawddit.webapp.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.pawddit.model.Comment;
import ar.edu.itba.pawddit.model.Group;
import ar.edu.itba.pawddit.model.Post;
import ar.edu.itba.pawddit.model.User;
import ar.edu.itba.pawddit.services.CommentService;
import ar.edu.itba.pawddit.services.CommentVoteService;
import ar.edu.itba.pawddit.services.GroupService;
import ar.edu.itba.pawddit.services.PostService;
import ar.edu.itba.pawddit.services.exceptions.NoPermissionsException;
import ar.edu.itba.pawddit.webapp.auth.PawdditUserDetailsService;
import ar.edu.itba.pawddit.webapp.dto.CommentDto;
import ar.edu.itba.pawddit.webapp.dto.PageCountDto;
import ar.edu.itba.pawddit.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.pawddit.webapp.exceptions.GroupNotFoundException;
import ar.edu.itba.pawddit.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.pawddit.webapp.form.CreateCommentForm;

@Path("/api/groups/{groupName}/posts/{postId}/comments")
@Component
public class CommentController {
	
	private static final int COMMENTS_PER_PAGE = 5;
	
	@Autowired
	private CommentVoteService cvs;
	
	@Autowired
	private GroupService gs;
	
	@Autowired
	private CommentService cs;
	
	@Autowired
	private PostService ps;
	
	@Autowired
	private PawdditUserDetailsService userDetailsService;
	
	@Context
	private UriInfo uriInfo;
	
	@GET
	@Produces(value = { MediaType.APPLICATION_JSON, })
	public Response getPostComments(
			@PathParam("groupName") final String groupName, 
			@PathParam("postId") final long postId,
			@QueryParam("page") @DefaultValue("1") Integer page) {
		
		try {
			final User user = userDetailsService.getLoggedUser();
			final Group group = gs.findByName(user, groupName).orElseThrow(GroupNotFoundException::new);
			final Post post = ps.findById(user, group, postId).orElseThrow(PostNotFoundException::new);
			if (page == 0) {
				final int count = (cs.findByPostCount(post)+COMMENTS_PER_PAGE-1)/COMMENTS_PER_PAGE;
				return Response.ok(PageCountDto.fromPageCount(count)).build();
			}
			else {
				final List<Comment> comments = cs.findByPost(user, post, COMMENTS_PER_PAGE, (page-1)*COMMENTS_PER_PAGE);
				return Response.ok(
					new GenericEntity<List<CommentDto>>(
						comments.stream()
							.map(CommentDto::fromCommentWithoutPost)
							.collect(Collectors.toList())
					) {}
				).build();
			}
		}
		catch (GroupNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (PostNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(value = { MediaType.APPLICATION_JSON, })
	public Response createComment(
			@Valid @FormDataParam("createComment") final CreateCommentForm form,
			@PathParam("groupName") final String groupName, 
			@PathParam("postId") final long postId) {

		try {
			final User user = userDetailsService.getLoggedUser();
			final Group group = gs.findByName(user, groupName).orElseThrow(GroupNotFoundException::new);
			final Post post = ps.findById(user, group, postId).orElseThrow(PostNotFoundException::new);
			if (user != null) {
				final Comment comment;
				if (form.getReplyTo() != null) {
					final Comment replyTo = cs.findById(user, post, form.getReplyTo()).orElseThrow(CommentNotFoundException::new);
					comment = cs.create(form.getContent(), post, replyTo, user, LocalDateTime.now());
				}
				else {
					comment = cs.create(form.getContent(), post, null, user, LocalDateTime.now());
				}
				final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(comment.getCommentid())).build();
				return Response.created(uri).build();
			}
			else {
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
		catch (GroupNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (PostNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (CommentNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/{commentId}")
	@Produces(value = { MediaType.APPLICATION_JSON, })
	public Response getComment(
			@PathParam("groupName") final String groupName, 
			@PathParam("postId") final long postId, 
			@PathParam("commentId") final long commentId) {
		
		try {
			final User user = userDetailsService.getLoggedUser();
			final Group group = gs.findByName(user, groupName).orElseThrow(GroupNotFoundException::new);
			final Post post = ps.findById(user, group, postId).orElseThrow(PostNotFoundException::new);
			final Comment comment = cs.findById(user, post, commentId).orElseThrow(CommentNotFoundException::new);
			return Response.ok(CommentDto.fromComment(comment)).build();
		}
		catch (GroupNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (PostNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (CommentNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@DELETE
	@Path("/{commentId}")
	@Produces(value = { MediaType.APPLICATION_JSON, })
	public Response deleteComment(
			@PathParam("groupName") final String groupName, 
			@PathParam("postId") final long postId, 
			@PathParam("commentId") final long commentId) {
		
		try {
			final User user = userDetailsService.getLoggedUser();
			final Group group = gs.findByName(user, groupName).orElseThrow(GroupNotFoundException::new);
			final Post post = ps.findById(user, group, postId).orElseThrow(PostNotFoundException::new);
			final Comment comment = cs.findById(user, post, commentId).orElseThrow(CommentNotFoundException::new);
			if (user != null) {
				cs.delete(user, group, post, comment);
				return Response.noContent().build();
			}
			else {
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
		catch (GroupNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (PostNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (CommentNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (NoPermissionsException e) {
			return Response.status(Status.FORBIDDEN).build();
		}
	}
	
	@PUT
	@Path("/{commentId}/upvote")
	@Produces(value = { MediaType.APPLICATION_JSON, })
	public Response commentUpVote(
			@PathParam("groupName") final String groupName, 
			@PathParam("postId") final long postId,
			@PathParam("commentId") final long commentId) {
		
		try {
			final User user = userDetailsService.getLoggedUser();
			final Group group = gs.findByName(user, groupName).orElseThrow(GroupNotFoundException::new);
			final Post post = ps.findById(user, group, postId).orElseThrow(PostNotFoundException::new);
			final Comment comment = cs.findById(user, post, commentId).orElseThrow(CommentNotFoundException::new);
			if (user != null) {
				cvs.upVote(user, comment);
				return Response.noContent().build();
			}
			else
				return Response.status(Status.BAD_REQUEST).build();
		}
		catch (GroupNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (PostNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (CommentNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@PUT
	@Path("/{commentId}/downvote")
	@Produces(value = { MediaType.APPLICATION_JSON, })
	public Response commentDownVote(
			@PathParam("groupName") final String groupName, 
			@PathParam("postId") final long postId,
			@PathParam("commentId") final long commentId) {
		
		try {
			final User user = userDetailsService.getLoggedUser();
			final Group group = gs.findByName(user, groupName).orElseThrow(GroupNotFoundException::new);
			final Post post = ps.findById(user, group, postId).orElseThrow(PostNotFoundException::new);
			final Comment comment = cs.findById(user, post, commentId).orElseThrow(CommentNotFoundException::new);
			if (user != null) {
				cvs.downVote(user, comment);
				return Response.noContent().build();
			}
			else
				return Response.status(Status.BAD_REQUEST).build();
		}
		catch (GroupNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (PostNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		catch (CommentNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

}
