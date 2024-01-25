package com.masai.service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.security.auth.login.LoginException;

import com.masai.repository.sessionRepository;
import com.masai.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.model.CurrentUserSession;
import com.masai.model.LoginDTO;
import com.masai.model.User;
import net.bytebuddy.utility.RandomString;

@Service
public class LoginServiceImpl implements LoginService{
	
	@Autowired
	private userRepository userRepository;
	
	@Autowired
	private sessionRepository sessionRepository;

	@Override
	public CurrentUserSession logIntoAccount(LoginDTO dto) throws LoginException {
		User user=userRepository.findByUserName(dto.getUsername());
		if(user==null) {
			throw new LoginException("Please Enter a valid username.");
		}
		Optional<CurrentUserSession> validUserSessionOpt =sessionRepository.findById(user.getUserLoginId());
		if(validUserSessionOpt.isPresent()) {
			throw new LoginException("User already Logged in with this username.");
		}
		if(user.getPassword().equals(dto.getPassword())) {
			String key=RandomString.make(6);
			String name= user.getFirstName().concat("  "+user.getLastName());
			CurrentUserSession currentUserSession=new CurrentUserSession(user.getUserLoginId(),name,key,LocalDateTime.now());
			sessionRepository.save(currentUserSession);
			return currentUserSession;
		}else {
			throw new LoginException("Please Enter a valid password");
		}
	}

	@Override
	public String logOutFromAccount(String key) throws LoginException {
		
		CurrentUserSession validUserSession=sessionRepository.findByUuid(key);
		
		if(validUserSession==null) {
			throw new LoginException("User not logged in with this username.");
		}
		sessionRepository.delete(validUserSession);
		return "Logged out successfully.";
	}

}