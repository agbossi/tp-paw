package ar.edu.itba.pawddit.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.pawddit.model.Group;
import ar.edu.itba.pawddit.model.User;
import ar.edu.itba.pawddit.services.GroupService;
import ar.edu.itba.pawddit.services.PostService;
import ar.edu.itba.pawddit.services.UserService;

@Controller
public class HelloWorldController {

	@Autowired
	private UserService us;
	
	@Autowired
	private GroupService gs;
	
	@Autowired
	private PostService ps;

	@RequestMapping("/")
	public ModelAndView index(@RequestParam(value = "userId", required = true) final int id) {
		final ModelAndView mav = new ModelAndView("index");
		mav.addObject("user", us.findById(id).orElseThrow(UserNotFoundException::new));
		return mav;
	}
	
	@RequestMapping("/welcome")
	public ModelAndView welcome() {
		final ModelAndView mav = new ModelAndView("welcome");
		mav.addObject("posts", ps.findAll());
		return mav;
	}
	
	@RequestMapping("/group/{groupName}")
	public ModelAndView group(@PathVariable final String groupName) {
		final ModelAndView mav = new ModelAndView("welcome");
		mav.addObject("posts", ps.findByGroup(new Group(groupName, null, null, null)));
		return mav;
	}
	
	@RequestMapping("/login")
	public ModelAndView login() {
		final ModelAndView mav = new ModelAndView("login");
		return mav;
	}
	
	@RequestMapping("/register")
	public ModelAndView register() {
		final ModelAndView mav = new ModelAndView("register");
		return mav;
	}
		
	@RequestMapping("/createUser")
	public ModelAndView create(@RequestParam(value = "name", required = true) final String username) {
		final User u = us.create(username, "a", "a", 0);
		return new ModelAndView("redirect:/?userId=" + u.getUserid());
	}
	
}
