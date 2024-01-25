package com.masai.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.masai.repository.sessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.exception.LoginException;
import com.masai.model.Admin;
import com.masai.model.AdminDto;
import com.masai.model.CurrentUserSession;
import net.bytebuddy.utility.RandomString;

@Service
public class AdminLoginServiceImpl implements AdminLoginService{

	@Autowired
	private sessionRepository sessionRepository;
	
	@Override
	public CurrentUserSession logIntoAccount(AdminDto dto) throws LoginException {

		Admin adm=new Admin();
		if(!adm.username.equalsIgnoreCase(dto.getUsername())) {
			throw new LoginException("Please Enter a valid Username");
		}
		Optional<CurrentUserSession> validUserSessionOpt =sessionRepository.findById(adm.id);
		if(validUserSessionOpt.isPresent()) {
			throw new LoginException("Admin already Logged in with this Username");
		}
		if(adm.password.equals(dto.getPassword())) {
			String key=RandomString.make(6);
			CurrentUserSession currentUserSession=new CurrentUserSession(adm.id,"admin",key,LocalDateTime.now());
			sessionRepository.save(currentUserSession);
			return currentUserSession;
		}else {
			throw new LoginException("Please Enter a valid Password");
		}
	}

	@Override
	public String logOutFromAccount(String key) throws LoginException {

		CurrentUserSession validUserSession=sessionRepository.findByUuid(key);
		if(validUserSession==null) {
			throw new LoginException("Admin not logged in with this Username.");
		}
		sessionRepository.delete(validUserSession);
		return "Logged out successfully";
	}

}