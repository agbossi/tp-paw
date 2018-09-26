package ar.edu.itba.pawddit.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.pawddit.model.User;
import ar.edu.itba.pawddit.services.PostService;
import ar.edu.itba.pawddit.services.UserService;
import ar.edu.itba.pawddit.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.pawddit.webapp.form.UserRegisterForm;

@Controller
public class UserController {
	
	@Autowired
	private UserService us;
	
	@Autowired
	private PostService ps;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@RequestMapping("/register")
	public ModelAndView register(@ModelAttribute("registerForm") final UserRegisterForm form) {
		return new ModelAndView("register");
	}
		
	@RequestMapping(value = "/register", method = { RequestMethod.POST })
	public ModelAndView registerPost(@Valid @ModelAttribute("registerForm") final UserRegisterForm form, final BindingResult errors, final HttpServletRequest request) {
		if(errors.hasErrors()) {
			return register(form);
		}
		
		final User user = us.create(form.getUsername(), form.getPassword(), form.getEmail(), 0);
	    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
	    authToken.setDetails(new WebAuthenticationDetails(request));
	     
	    Authentication authentication = authenticationManager.authenticate(authToken);
	     
	    SecurityContextHolder.getContext().setAuthentication(authentication);
		return new ModelAndView("redirect:/");
	}
	
	@RequestMapping("/login")
	public ModelAndView login(@RequestParam(value = "error", required=false) final boolean error) {
		ModelAndView mav = new ModelAndView("login");
		
		if(error) {
			mav.addObject("loginError", new Boolean(true));
			return mav;
		}
		
		mav.addObject("loginError", new Boolean(false));
		return mav;
		
	}
	
	@RequestMapping("/profile/{username}")
	public ModelAndView profile(@PathVariable final String username) {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User userProfile = us.findByUsername(username).orElseThrow(UserNotFoundException::new);
		final ModelAndView mav = new ModelAndView("profile");
		
		mav.addObject("userProfile", userProfile);
		mav.addObject("posts", ps.findByUser(userProfile));
		
		if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
			mav.addObject("user", us.findByUsername(auth.getName()).orElseThrow(UserNotFoundException::new));
		}
		return mav;
	}
}
