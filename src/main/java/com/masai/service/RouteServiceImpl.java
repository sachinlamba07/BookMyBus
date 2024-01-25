package com.masai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.masai.repository.routeRepository;
import com.masai.repository.sessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.exception.RouteException;
import com.masai.exception.UserException;
import com.masai.model.Bus;
import com.masai.model.CurrentUserSession;
import com.masai.model.Route;

@Service
public class RouteServiceImpl implements RouteService{
	
	@Autowired
	private routeRepository routeRepository;
	
	@Autowired
	private sessionRepository sessionRepository;

	@Override
	public Route addRoute(Route route,String key) throws RouteException, UserException {
		CurrentUserSession loggedInUser=sessionRepository.findByUuid(key);
		
		if(loggedInUser==null) {
			throw new UserException("Please provide a valid key to add Route");
		}
		
		if (loggedInUser.getType().equalsIgnoreCase("Admin")) {
			
			Route newRoute = routeRepository.findByRouteFromAndRouteTo(route.getRouteFrom(), route.getRouteTo());
			if (newRoute != null)
				throw new RouteException("Route from : " + route.getRouteFrom() + " to " + route.getRouteTo() + " already exists" );

			List<Bus> busList = new ArrayList<>();
			route.setBuslist(busList);
		
			return  routeRepository.save(route);
		}
		else throw new UserException("Access denied");
	}

	@Override
	public Route updateRoute(Route route,String key) throws RouteException, UserException {
		
		CurrentUserSession loggedInUser=sessionRepository.findByUuid(key);
		
		if(loggedInUser==null) {
			throw new UserException("Please provide a valid key to update Route");
		}
		
		if (loggedInUser.getType().equalsIgnoreCase("Admin")) {
				Optional<Route> opt = routeRepository.findById(route.getRouteId());
				if(opt.isPresent()) {
					   
					 Route existingRoute = opt.get();
					 
					 if (!existingRoute.getBuslist().isEmpty())
						 throw new RouteException("Cannot update Route ! Already buses are Scheduled for this route");
					 
					 if (route.getDistance() != null) existingRoute.setDistance(route.getDistance());
					 if (route.getRouteFrom() != null) existingRoute.setRouteFrom(route.getRouteFrom());
					 if (route.getRouteTo() != null) existingRoute.setRouteTo(route.getRouteTo());

                    return routeRepository.save(existingRoute);
				  }
				  else {
					 throw new RouteException("No route exist to update please save the Route first");
				  }
		}
		else throw new UserException("Access denied");

	}

	@Override
	public Route deleteRoute(Integer routeId,String key) throws RouteException, UserException {
		
		CurrentUserSession loggedInUser=sessionRepository.findByUuid(key);
		
		if(loggedInUser==null) {
			throw new UserException("Please provide a valid key to delete Route");
		}
		
		if (loggedInUser.getType().equalsIgnoreCase("Admin")) {

				Optional<Route> opt =	routeRepository.findById(routeId);
				
				if(opt.isPresent()) {
					Route rot = opt.get();
					if (!rot.getBuslist().isEmpty())
						 throw new RouteException("Cannot delete Route ! Already buses are Scheduled for this route");
					routeRepository.delete(rot);
					return rot;
				}
				else {
					throw new RouteException("No route found on this "+routeId+" id");
				}
		}
		throw new UserException("Access denied");
		
	
	}

	@Override
	public Route viewRoute(Integer routeId,String key) throws RouteException, UserException {
		
		CurrentUserSession loggedInUser=sessionRepository.findByUuid(key);
		
		if(loggedInUser==null) {
			throw new UserException("Please provide a valid key to view Route");
		}
			Optional<Route> opt =routeRepository.findById(routeId);
		     if(opt.isPresent()) {
		    	 
		    	 return opt.get();
		     }
		     else {
		    	 throw new RouteException("No route found on this "+routeId+" id");
		     }
			
	}

	@Override
	public List<Route> viewAllRoute(String key) throws RouteException, UserException {
		
		CurrentUserSession loggedInUser=sessionRepository.findByUuid(key);
		
		if(loggedInUser==null) {
			throw new UserException("Please provide a valid key to view Route");
		}
			
			List<Route> routeList = routeRepository.findAll();
			if(!routeList.isEmpty()) {
				
				return routeList;
			}else {
				throw new RouteException("Route list is empty");
			}
	}

}
