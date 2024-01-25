package com.masai.exception;

import java.time.LocalDateTime;


import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.el.MethodNotFoundException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(RouteException.class)
	public ResponseEntity<ErrorDetails> myRouteException(RouteException re, WebRequest webReq){

		ErrorDetails ed = new ErrorDetails();
		ed.setTimestamp(LocalDateTime.now());
		ed.setMessage(re.getMessage());
		ed.setDetails(webReq.getDescription(false));
		log.error("RouteException Occour!");
		return new ResponseEntity<ErrorDetails>(ed, HttpStatus.BAD_REQUEST);
		
	}


	@ExceptionHandler(UserException.class)
	public ResponseEntity<ErrorDetails> userExceptionHandler(UserException ue,WebRequest req){

		ErrorDetails ed=new ErrorDetails();
		ed.setTimestamp(LocalDateTime.now());
		ed.setMessage(ue.getMessage());
		ed.setDetails(req.getDescription(false));
		return new ResponseEntity<ErrorDetails>(ed,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(LoginException.class)
	public ResponseEntity<ErrorDetails> loginExceptionHandler(LoginException le,WebRequest req){
		
		le.printStackTrace();

		ErrorDetails ed=new ErrorDetails();
		ed.setTimestamp(LocalDateTime.now());
		ed.setMessage(le.getMessage());
		ed.setDetails(req.getDescription(false));
		return new ResponseEntity<ErrorDetails>(ed,HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> otherExceptionHandler(Exception se, WebRequest req){

		ErrorDetails ed= new ErrorDetails();
			ed.setTimestamp(LocalDateTime.now());
			ed.setMessage(se.getMessage());
			ed.setDetails(req.getDescription(false));
				
		return new ResponseEntity<ErrorDetails>(ed, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}


	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> myMNVEHandler(MethodArgumentNotValidException me) {

		ErrorDetails ed= new ErrorDetails();
		ed.setTimestamp(LocalDateTime.now());
		ed.setMessage(me.getBindingResult().getFieldError().getDefaultMessage());
		ed.setDetails(me.getMessage());
		
		return new ResponseEntity<ErrorDetails>(ed,HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FeedbackException.class)
	public ResponseEntity<ErrorDetails> myRouteException(FeedbackException re, WebRequest webReq){

		ErrorDetails ed = new ErrorDetails();
		ed.setTimestamp(LocalDateTime.now());
		ed.setMessage(re.getMessage());
		ed.setDetails(webReq.getDescription(false));
		
		return new ResponseEntity<ErrorDetails>(ed, HttpStatus.BAD_REQUEST);
		
	}
	
	

	@ExceptionHandler(BusException.class)
	public ResponseEntity<ErrorDetails> myBusException(BusException be, WebRequest webReq){
		
		ErrorDetails ed = new ErrorDetails();
		ed.setTimestamp(LocalDateTime.now());
		ed.setMessage(be.getMessage());
		ed.setDetails(webReq.getDescription(false));
		
		return new ResponseEntity<ErrorDetails>(ed, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorDetails> NotFoundExceptionHandler(NotFoundException ne,WebRequest req){
		
		ErrorDetails ed=new ErrorDetails();
		ed.setTimestamp(LocalDateTime.now());
		ed.setMessage(ne.getMessage());
		ed.setDetails(req.getDescription(false));
		return new ResponseEntity<ErrorDetails>(ed,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodNotFoundException.class)
	public ResponseEntity<ErrorDetails> methodNotFoundExceptionHandler(MethodNotFoundException me,WebRequest req){
		
		ErrorDetails ed=new ErrorDetails();
		ed.setTimestamp(LocalDateTime.now());
		ed.setMessage(me.getMessage());
		ed.setDetails(req.getDescription(false));
		return new ResponseEntity<ErrorDetails>(ed,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ReservationException.class)
	public ResponseEntity<ErrorDetails> reservationExceptionHandler(ReservationException se, WebRequest req){
	
		ErrorDetails ed= new ErrorDetails();
			ed.setTimestamp(LocalDateTime.now());
			ed.setMessage(se.getMessage());
			ed.setDetails(req.getDescription(false));
				
		return new ResponseEntity<ErrorDetails>(ed, HttpStatus.BAD_REQUEST);
		
	}
	
	
	@ExceptionHandler(DateTimeException.class)
	public ResponseEntity<ErrorDetails> DateTimeExceptionHandler(DateTimeException se, WebRequest req){
	
		ErrorDetails ed= new ErrorDetails();
			ed.setTimestamp(LocalDateTime.now());
			ed.setMessage(se.getMessage());
			ed.setDetails(req.getDescription(false));
				
		return new ResponseEntity<ErrorDetails>(ed, HttpStatus.BAD_REQUEST);
		
	}
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorDetails> NoHandlerFoundExceptionHandler(NoHandlerFoundException se, WebRequest req){
	
		ErrorDetails ed= new ErrorDetails();
			ed.setTimestamp(LocalDateTime.now());
			ed.setMessage(se.getMessage());
			ed.setDetails(req.getDescription(false));
				
		return new ResponseEntity<ErrorDetails>(ed, HttpStatus.BAD_REQUEST);
		
	}
}
