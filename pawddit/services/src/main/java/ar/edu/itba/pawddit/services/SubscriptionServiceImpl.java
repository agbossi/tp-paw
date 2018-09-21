package ar.edu.itba.pawddit.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.pawddit.model.Group;
import ar.edu.itba.pawddit.model.User;
import ar.edu.itba.pawddit.persistence.SubscriptionDao;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
	
	@Autowired
	SubscriptionDao sd;
	
	@Override
	public Boolean suscribe(final User user, final Group group) {
		return sd.suscribe(user, group);
	}

	@Override
	public Boolean unsuscribe(final User user, final Group group) {
		return sd.unsuscribe(user, group);
	}

	@Override
	public Boolean isUserSub(final User user, final Group group) {
		return sd.isUserSub(user, group);
	}

}