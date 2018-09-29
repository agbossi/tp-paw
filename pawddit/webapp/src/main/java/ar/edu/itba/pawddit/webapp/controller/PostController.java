package ar.edu.itba.pawddit.webapp.controller;

import java.io.IOException;
import java.sql.Timestamp;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.pawddit.model.Group;
import ar.edu.itba.pawddit.model.Post;
import ar.edu.itba.pawddit.model.User;
import ar.edu.itba.pawddit.services.CommentService;
import ar.edu.itba.pawddit.services.GroupService;
import ar.edu.itba.pawddit.services.ImageService;
import ar.edu.itba.pawddit.services.PostService;
import ar.edu.itba.pawddit.services.PostVoteService;
import ar.edu.itba.pawddit.webapp.exceptions.GroupNotFoundException;
import ar.edu.itba.pawddit.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.pawddit.webapp.form.CreateCommentForm;
import ar.edu.itba.pawddit.webapp.form.CreatePostForm;
import ar.edu.itba.pawddit.webapp.form.CreatePostNoGroupForm;

@Controller
public class PostController extends BaseController {
	
	private static final int COMMENTS_PER_PAGE = 5;
	
	@Autowired
	private GroupService gs;
	
	@Autowired
	private PostService ps;
	
	@Autowired
	private CommentService cs;
	
	@Autowired
	private PostVoteService pvs;
	
	@Autowired
	private ImageService is;
	
	@RequestMapping("/createPost")
	public ModelAndView createPost(@ModelAttribute("createPostForm") final CreatePostNoGroupForm form) {
		final ModelAndView mav = new ModelAndView("createPost");
		mav.addObject("groups", gs.findAll());
		return mav;
	}
	
	@RequestMapping(value = "/createPost", method = { RequestMethod.POST })
	public ModelAndView createPostPost(@Valid @ModelAttribute("createPostForm") final CreatePostNoGroupForm form, final BindingResult errors, @ModelAttribute("user") final User user, @RequestParam("file") MultipartFile file) {
		if(errors.hasErrors()) {
			return createPost(form);
		}
		
		String imageId = null;
		if (!file.isEmpty()) {
			try {
				byte[] image = file.getBytes();
				imageId = is.saveImage(image);
			} catch (IOException e) {
				
			}
		}

		final Group g = gs.findByName(form.getGroupName()).orElseThrow(GroupNotFoundException::new);
		final Post p = ps.create(form.getTitle(), form.getContent(), new Timestamp(System.currentTimeMillis()), g, user, imageId);
		final ModelAndView mav = new ModelAndView("redirect:/group/" + g.getName() + "/" + p.getPostid());
		return mav;
	}
	
	@RequestMapping("/group/{groupName}/createPost")
	public ModelAndView createPost(@PathVariable final String groupName, @ModelAttribute("createPostForm") final CreatePostForm form) {
		final ModelAndView mav = new ModelAndView("createPost");
		mav.addObject("group", gs.findByName(groupName).orElseThrow(GroupNotFoundException::new));
		return mav;
	}

	
	@RequestMapping(value = "/group/{groupName}/createPost", method = { RequestMethod.POST })
	public ModelAndView createPostPost(@PathVariable final String groupName, @Valid @ModelAttribute("createPostForm") final CreatePostForm form, final BindingResult errors, @ModelAttribute("user") final User user, @RequestParam("file") MultipartFile file) {
		if(errors.hasErrors()) {
			return createPost(groupName, form);
		}
		
		String imageId = null;
		if (!file.isEmpty()) {
			try {
				byte[] image = file.getBytes();
				imageId = is.saveImage(image);
			} catch (IOException e) {
				
			}
		}

		final Group g = gs.findByName(groupName).orElseThrow(GroupNotFoundException::new);
		final Post p = ps.create(form.getTitle(), form.getContent(), new Timestamp(System.currentTimeMillis()), g, user, imageId);
		final ModelAndView mav = new ModelAndView("redirect:/group/" + g.getName() + "/" + p.getPostid());
		return mav;
	}
	
	@RequestMapping("/group/{groupName}/{postId}")
	public ModelAndView showPost(@PathVariable final String groupName, @PathVariable final Integer postId, @RequestParam(defaultValue = "1", value="page") int page, @ModelAttribute("createCommentForm") final CreateCommentForm form, @ModelAttribute("user") final User user) {
		final ModelAndView mav = new ModelAndView("post");
		final Group group = gs.findByName(groupName).orElseThrow(GroupNotFoundException::new);
		final Post post = ps.findById(group, postId).orElseThrow(PostNotFoundException::new);
		if (user != null) {
			final Integer vote = pvs.checkVote(user, post);
			mav.addObject("vote", vote);
		}
		mav.addObject("group", group);
		mav.addObject("post", post);
		mav.addObject("comments", cs.findByPost(post, COMMENTS_PER_PAGE, (page-1)*COMMENTS_PER_PAGE));
		mav.addObject("commentsPage", page);
		mav.addObject("commentsPageCount", (cs.findByPostCount(post)+COMMENTS_PER_PAGE-1)/COMMENTS_PER_PAGE);
		return mav;
	}
	
	@RequestMapping(value = "/group/{groupName}/{postId}/createComment", method = { RequestMethod.POST })
	public ModelAndView showPost(@PathVariable final String groupName, @PathVariable final Integer postId, @Valid @ModelAttribute("createCommentForm") final CreateCommentForm form, final BindingResult errors, @ModelAttribute("user") final User user) {
		if(errors.hasErrors()) {
			return showPost(groupName, postId, 1, form, user);
		}

		final Group g = gs.findByName(groupName).orElseThrow(GroupNotFoundException::new);
		final Post p = ps.findById(g, postId).orElseThrow(PostNotFoundException::new);
		cs.create(form.getContent(), p, null, user, new Timestamp(System.currentTimeMillis()));
		
		final ModelAndView mav = new ModelAndView("redirect:/group/" + g.getName() + "/" + p.getPostid());
		return mav;
	}
	
	@RequestMapping(value="/group/{groupName}/{postId}/upvote", method = {RequestMethod.POST})
	public ModelAndView upvotePost(@PathVariable final Integer postId, @PathVariable final String groupName, @ModelAttribute("user") final User user) {
		final Group g = gs.findByName(groupName).orElseThrow(GroupNotFoundException::new);
		final Post p = ps.findById(g, postId).orElseThrow(PostNotFoundException::new);
		pvs.votePost(user, p, 1);
		
		final ModelAndView mav = new ModelAndView("redirect:/group/" + g.getName() + "/" + p.getPostid());
		
		return mav;
	}
	
	@RequestMapping(value="/group/{groupName}/{postId}/downvote", method = {RequestMethod.POST})
	public ModelAndView downvotePost(@PathVariable final Integer postId, @PathVariable final String groupName, @ModelAttribute("user") final User user) {
		final Group g = gs.findByName(groupName).orElseThrow(GroupNotFoundException::new);
		final Post p = ps.findById(g, postId).orElseThrow(PostNotFoundException::new);
		pvs.votePost(user, p, -1);
		
		final ModelAndView mav = new ModelAndView("redirect:/group/" + g.getName() + "/" + p.getPostid());
		
		return mav;
	}
	
	@RequestMapping(value="/group/{groupName}/{postId}/cancelVote", method = {RequestMethod.POST})
	public ModelAndView cancelVotePost(@PathVariable final Integer postId, @PathVariable final String groupName, @ModelAttribute("user") final User user) {
		final Group g = gs.findByName(groupName).orElseThrow(GroupNotFoundException::new);
		final Post p = ps.findById(g, postId).orElseThrow(PostNotFoundException::new);
		pvs.cancelVote(user, p);
		
		final ModelAndView mav = new ModelAndView("redirect:/group/" + g.getName() + "/" + p.getPostid());
		
		return mav;
	}
	
	@RequestMapping(value="/group/{groupName}/{postId}/changeVote", method = {RequestMethod.POST})
	public ModelAndView changeVotePost(@PathVariable final Integer postId, @PathVariable final String groupName, @ModelAttribute("user") final User user) {
		final Group g = gs.findByName(groupName).orElseThrow(GroupNotFoundException::new);
		final Post p = ps.findById(g, postId).orElseThrow(PostNotFoundException::new);
		pvs.changeVote(user, p);
		
		final ModelAndView mav = new ModelAndView("redirect:/group/" + g.getName() + "/" + p.getPostid());
		
		return mav;
	}
	
}
