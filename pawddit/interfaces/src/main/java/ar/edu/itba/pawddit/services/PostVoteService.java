package ar.edu.itba.pawddit.services;

import ar.edu.itba.pawddit.model.Post;
import ar.edu.itba.pawddit.model.User;

public interface PostVoteService {
	
	public Boolean votePost(User user, Post post, Integer value);
	public Boolean changeVote(User user, Post post);
	public Boolean cancelVote(User user, Post post);
	public Integer checkVote(User user, Post post);

}