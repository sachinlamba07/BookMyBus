package com.masai.service;

import java.util.List;
import java.util.Objects;

import com.masai.repository.busRepository;
import com.masai.repository.feedbackRepository;
import com.masai.repository.sessionRepository;
import com.masai.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.exception.BusException;
import com.masai.exception.FeedbackException;
import com.masai.exception.UserException;
import com.masai.model.Bus;
import com.masai.model.CurrentUserSession;
import com.masai.model.Feedback;
import com.masai.model.User;

@Service
public class FeedbackServiceImpl implements FeedbackService{

	@Autowired
	private feedbackRepository feedbackRepository;
	
	@Autowired
	private userRepository userRepository;
	
	@Autowired
	private busRepository busRepository;
	
	@Autowired
	private sessionRepository sessionRepository;
	
	@Override
	public String addFeedback( Integer busId, Feedback feedback,String key) throws FeedbackException, UserException, BusException {
		CurrentUserSession loggedInUser=sessionRepository.findByUuid(key);
		if(loggedInUser==null) {
			throw new UserException("Please provide a valid key to add Feedback");
		}
		User user = userRepository.findById(loggedInUser.getUserId()).orElseThrow(() -> new UserException("User with Id " + loggedInUser.getUserId() + " not found"));
		if(Objects.equals(user.getUserLoginId(), loggedInUser.getUserId())) {
			Bus b = busRepository.findById(busId).orElseThrow(() -> new BusException("Bus with Id " + busId + " not found"));
			
			feedback.setBus(b);
			feedback.setUser(user);
			
			Feedback f = feedbackRepository.save(feedback);
			
			return "Feedback Added SucessFully";
		}else throw new UserException("Invalid User Id");

	}

	@Override
	public Feedback updateFeedback( Feedback feedback,String key) throws FeedbackException, UserException {
		
		CurrentUserSession loggedInUser=sessionRepository.findByUuid(key);
		if(loggedInUser==null) {
			throw new UserException("Please provide a valid key to update user");
		}
		User user = userRepository.findById(loggedInUser.getUserId()).orElseThrow(() -> new UserException("User with Id " + loggedInUser.getUserId() + " not found"));
		if(Objects.equals(user.getUserLoginId(), loggedInUser.getUserId())) {
			Feedback f = feedbackRepository.findById(feedback.getFeedbackId()).orElseThrow(() -> new FeedbackException("Feedback with Id " + feedback.getFeedbackId() + " does not exist"));
			
			if (feedback.getComments() != null) f.setComments(feedback.getComments());
			if (feedback.getDriverRating() != null) f.setDriverRating(feedback.getDriverRating());
			if (feedback.getServiceRating() != null) f.setServiceRating(feedback.getServiceRating());
			if (feedback.getOverallRating() != null) f.setOverallRating(feedback.getOverallRating());

            return feedbackRepository.save(f);
		}else throw new UserException("Invalid User Id");
		
	}

	@Override
	public Feedback viewFeedback(Integer feedbackId,String key) throws FeedbackException, UserException {
		CurrentUserSession loggedInUser=sessionRepository.findByUuid(key);
		if(loggedInUser==null) {
			throw new UserException("Please provide a valid key to update user");
		}
		User user = userRepository.findById(loggedInUser.getUserId()).orElseThrow(() -> new UserException("User with Id " + loggedInUser.getUserId() + " not found"));
		if(Objects.equals(user.getUserLoginId(), loggedInUser.getUserId())) {
            return feedbackRepository.findById(feedbackId).orElseThrow(() -> new FeedbackException("Feedback with Id " + feedbackId + " does not exist"));
		}else throw new UserException("Invalid User Id");
		
	}

	@Override
	public List<Feedback> viewAllFeedback(String key) throws FeedbackException, UserException {
		CurrentUserSession loggedInUser=sessionRepository.findByUuid(key);
		if(loggedInUser==null) {
			throw new UserException("Please provide a valid key to update user");
		}
		User user = userRepository.findById(loggedInUser.getUserId()).orElseThrow(() -> new UserException("User with Id " + loggedInUser.getUserId() + " not found"));
		if(user.getUserLoginId().equals(loggedInUser.getUserId())) {
			List<Feedback> f= feedbackRepository.findAll();
			
			if (!f.isEmpty()) return f;
			else throw new FeedbackException("Feedback not found");
		}else throw new UserException("Invalid User Id");
		
	}

}
