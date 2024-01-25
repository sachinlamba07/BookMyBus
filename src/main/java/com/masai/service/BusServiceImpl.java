package com.masai.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.masai.repository.busRepository;
import com.masai.repository.routeRepository;
import com.masai.repository.sessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.exception.BusException;
import com.masai.exception.UserException;
import com.masai.model.Bus;
import com.masai.model.CurrentUserSession;
import com.masai.model.Route;

@Service
public class BusServiceImpl implements BusService {

	@Autowired
	private busRepository busRepository;

	@Autowired
	private sessionRepository sessionRepository;

	@Autowired
	routeRepository routeRepository;

	@Override
	public Bus addBus(Bus bus, String key) throws BusException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to add Bus");
		}
		if (loggedInUser.getType().equalsIgnoreCase("Admin")) {
			Route route = routeRepository.findByRouteFromAndRouteTo(bus.getRouteFrom(), bus.getRouteTo());
			System.out.println(bus);
			System.out.println(route);
			if (route != null) {
				if (route.getBuslist().contains(bus)) {
					throw new BusException("Bus already exists");
				}
				route.getBuslist().add(bus);
				bus.setRoute(route);
				return busRepository.save(bus);
			} else
				throw new BusException("Cannot find route for adding Bus");

		} else
			throw new UserException("Unauthorized Access! Only Admin can add bus");

	}
	@Override
	public Bus updateBus(Bus bus, String key) throws BusException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to update Bus");
		}
		if (loggedInUser.getType().equalsIgnoreCase("Admin")) {
			Optional<Bus> opt = busRepository.findById(bus.getBusId());
			if (opt.isPresent()) {
				Bus curr = opt.get();
				if (!Objects.equals(curr.getAvailableSeats(), curr.getSeats()))
					throw new BusException("Cannot update Bus already scheduled");

				Route route = routeRepository.findByRouteFromAndRouteTo(curr.getRouteFrom(), curr.getRouteTo());

				if (bus.getRouteFrom() != null && bus.getRouteTo() != null) {
					route = routeRepository.findByRouteFromAndRouteTo(bus.getRouteFrom(), bus.getRouteTo());

					if (route == null)
						throw new BusException("Invalid route details");
				}
				if (bus.getArrivalTime() != null)
					curr.setArrivalTime(bus.getArrivalTime());
				if (bus.getAvailableSeats() != null)
					curr.setAvailableSeats(bus.getAvailableSeats());
				if (bus.getBusName() != null)
					curr.setBusName(bus.getBusName());
				if (bus.getBusType() != null)
					curr.setBusType(bus.getBusType());
				if (bus.getDepartureTime() != null)
					curr.setDepartureTime(bus.getDepartureTime());
				if (bus.getDriverName() != null)
					curr.setDriverName(bus.getDriverName());
				if (bus.getRouteFrom() != null)
					curr.setRouteFrom(bus.getRouteFrom());
				if (bus.getRouteTo() != null)
					curr.setRouteTo(bus.getRouteTo());
				if (bus.getSeats() != null)
					curr.setSeats(bus.getSeats());

				Bus updated = busRepository.save(curr);
				route.getBuslist().add(updated);
				route.getBuslist().remove(bus);

				return updated;

			}
			throw new BusException("Bus with id " + bus.getBusId() + "does not exist");

		} else
			throw new UserException("Unauthorized Access! Only Admin can make changes");

	}

	@Override
	public Bus deleteBus(Integer busId, String key) throws BusException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to delete Bus");
		}
		if (loggedInUser.getType().equalsIgnoreCase("Admin")) {
			Optional<Bus> opt = busRepository.findById(busId);
			if (opt.isPresent()) {
				Bus exbus = opt.get();

				if (!Objects.equals(exbus.getAvailableSeats(), exbus.getSeats()))
					throw new BusException("Cannot delete Bus already scheduled");

				busRepository.delete(exbus);
				return exbus;
			}

			throw new BusException("bus doesn't exists with this " + busId + " id");
		} else
			throw new UserException("Unauthorized Access! Only Admin can delete Bus");

	}

	@Override
	public Bus viewBus(Integer busId, String key) throws BusException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to view bus");
		}
		Optional<Bus> opt = busRepository.findById(busId);
		if (opt.isPresent()) {

            return opt.get();
		}
		throw new BusException("Bus doesn't exists with this " + busId + " id");
	}

	@Override
	public List<Bus> viewBusByType(String busType, String key) throws BusException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to view buses");
		}
		List<Bus> bList = busRepository.findByBusType(busType);
		if (bList.isEmpty()) {
			throw new BusException("bus list is empty");
		}
		return bList;
	}

	@Override
	public List<Bus> viewAllBus(String key) throws BusException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to view buses");
		}
		List<Bus> buslist = busRepository.findAll();
		if (buslist.isEmpty()) {
			throw new BusException("bus list is empty");
		}
		return buslist;
	}
}
