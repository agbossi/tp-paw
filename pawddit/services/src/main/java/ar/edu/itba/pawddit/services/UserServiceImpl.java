package ar.edu.itba.pawddit.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.pawddit.persistence.UserDao;
import ar.edu.itba.pawddit.services.UserService;
import ar.edu.itba.pawddit.services.exceptions.RepeatedData;
import ar.edu.itba.pawddit.services.exceptions.UserRepeatedDataException;
import ar.edu.itba.pawddit.model.User;
import ar.edu.itba.pawddit.model.VerificationToken;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Optional<User> findById(final long id) {
		return userDao.findById(id);
	}

	@Override
	public User create(final String username, final String password, final String email) throws UserRepeatedDataException {
		
		UserRepeatedDataException repeatedDataException = new UserRepeatedDataException();
		boolean repeatedUsername = false;
		boolean repeatedEmail = false;
		
		if (findByUsername(username).isPresent()) {
			repeatedUsername = true;
			repeatedDataException.addRepeatedData(RepeatedData.REPEATED_USERNAME);
		}
		
		if (findByEmail(email).isPresent()) {
			repeatedEmail = true;
			repeatedDataException.addRepeatedData(RepeatedData.REPEATED_EMAIL);
		}
		
		if(repeatedEmail || repeatedUsername)
			throw repeatedDataException;
		
		return userDao.create(username, passwordEncoder.encode(password), email);
	}

	@Override
	public Optional<User> findByUsername(final String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public Optional<User> findByEmail(final String email) {
		return userDao.findByEmail(email);
	}

	@Override
	public VerificationToken createToken(final User user) {
		return userDao.createToken(user);
	}

	@Override
	public Optional<VerificationToken> findToken(final String token) {
		Optional <VerificationToken> vt = userDao.findToken(token);
		if (vt.isPresent())
			userDao.deleteToken(token);
		return vt;
	}

	@Override
	public int enableUser(final User user) {
		return userDao.enableUser(user);	
	}
	
}
