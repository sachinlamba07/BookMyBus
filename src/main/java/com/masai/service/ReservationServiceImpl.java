package com.masai.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.masai.repository.busRepository;
import com.masai.repository.reservationRepository;
import com.masai.repository.sessionRepository;
import com.masai.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.exception.BusException;
import com.masai.exception.ReservationException;
import com.masai.exception.UserException;
import com.masai.model.Bus;
import com.masai.model.CurrentUserSession;
import com.masai.model.Reservation;
import com.masai.model.User;

@Service
public class ReservationServiceImpl implements ReservationService {

	@Autowired
	private reservationRepository rDao;

	@Autowired
	private busRepository busRepository;

	@Autowired
	private sessionRepository sessionRepository;

	@Autowired
	private userRepository userRepository;

	@Override
	public Reservation addNewReservation(Integer busId, Reservation reservation, String key)
			throws ReservationException, BusException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to add reservation");
		}

		User u = userRepository.findById(loggedInUser.getUserId()).orElseThrow(
				() -> new UserException("User with User Id " + loggedInUser.getUserId() + " does not exist"));

		Bus b = busRepository.findById(busId).orElseThrow(() -> new BusException("Bus with Id " + busId + " not found"));

		if (!reservation.getSource().equalsIgnoreCase(b.getRouteFrom())
				|| !reservation.getDestination().equalsIgnoreCase(b.getRouteTo()))
			throw new ReservationException("Bus is not available for this route");

		if (b.getAvailableSeats() <= 0)
			throw new ReservationException("Seats are not available");

		b.setAvailableSeats(b.getAvailableSeats() - 1);

		reservation.setReservationType("Online");
		reservation.setReservationStatus("Booked");
		reservation.setReservationTime(LocalTime.parse(LocalTime.now().toString()));
		reservation.setBus(b);

		u.setReservation(reservation);

		return rDao.save(reservation);

	}

	@Override
	public Reservation updateReservation(Reservation reservation, String key)
			throws ReservationException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to add reservation");
		}

		Optional<Reservation> opt = rDao.findById(reservation.getReservationId());

		if (opt.isPresent()) {
			User u = userRepository.findById(loggedInUser.getUserId()).orElseThrow(
					() -> new UserException("User with User Id " + loggedInUser.getUserId() + " does not exist"));

			Reservation curr = getReservation(reservation, u);

			Reservation updateReservation = rDao.save(curr);

			u.setReservation(updateReservation);

			return updateReservation;

		} else {

			throw new ReservationException("Reservation not found");

		}

	}

	private static Reservation getReservation(Reservation reservation, User u) {
		Reservation curr = u.getReservation();

		if (reservation.getDestination() != null)
			curr.setDestination(reservation.getDestination());
		if (reservation.getReservationDate() != null)
			curr.setReservationDate(reservation.getReservationDate());
		if (reservation.getReservationStatus() != null)
			curr.setReservationStatus(reservation.getReservationStatus());
		if (reservation.getReservationTime() != null)
			curr.setReservationTime(LocalTime.parse(reservation.getReservationTime().toString()));
		if (reservation.getReservationType() != null)
			curr.setReservationType(reservation.getReservationType());
		if (reservation.getSource() != null)
			curr.setSource(reservation.getSource());
		return curr;
	}

	@Override
	public Reservation deleteReservation(Integer reservationId, String key) throws ReservationException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to add reservation");
		}

		Optional<Reservation> opt = rDao.findById(reservationId);

		if (opt.isPresent()) {

			Reservation existingReservation = opt.get();
			Optional<User> u = userRepository.findById(loggedInUser.getUserId());

			User currUser = u.get();

			Bus b = busRepository.findById(currUser.getReservation().getBus().getBusId())
					.orElseThrow(() -> new ReservationException(
							"Bus with Id " + currUser.getReservation().getBus().getBusId() + " not found"));
			b.setAvailableSeats(b.getAvailableSeats() + 1);

			currUser.setReservation(null);

			rDao.delete(existingReservation);

			return existingReservation;

		} else {

			throw new ReservationException("Reservation is not present with this Reservation ID :" + reservationId);

		}

	}

	@Override
	public Reservation viewReservationById(Integer reservationId, String key)
			throws ReservationException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to add reservation");
		}

		Optional<Reservation> opt = rDao.findById(reservationId);

		if (opt.isPresent()) {

			if (loggedInUser.getType().equalsIgnoreCase("Admin")) {

                return opt.get();
			} else {
				User u = userRepository.findById(loggedInUser.getUserId()).orElseThrow(
						() -> new UserException("User with User Id " + loggedInUser.getUserId() + " does not exist"));
				if (Objects.equals(u.getUserLoginId(), loggedInUser.getUserId())) {

                    return opt.get();
				} else {
					throw new UserException("Invalid User details, please login first");
				}
			}

		} else {

			throw new ReservationException("Reservation is not present with this Reservation ID :" + reservationId);

		}
	}

	@Override
	public List<Reservation> viewAllReservation(String key) throws ReservationException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to view reservation");
		}
		if (loggedInUser.getType().equalsIgnoreCase("Admin")) {

			List<Reservation> reservation = rDao.findAll();

			if (!reservation.isEmpty()) {

				return reservation;
			} else {
				throw new ReservationException("Reservation not found");
			}
		} else
			throw new ReservationException("Access Denied");

	}

	@Override
	public List<Reservation> getAllReservationByDate(LocalDate date, String key)
			throws ReservationException, UserException {

		CurrentUserSession loggedInUser = sessionRepository.findByUuid(key);
		if (loggedInUser == null) {
			throw new UserException("Please provide a valid key to add reservation");
		}
		if (loggedInUser.getType().equalsIgnoreCase("Admin")) {

			List<Reservation> reservation = rDao.findAll();

			List<Reservation> reservationByDate = new ArrayList<>();

			if (!reservation.isEmpty()) {

				for (Reservation s : reservation) {

					if (date.isEqual(s.getReservationDate())) {

						reservationByDate.add(s);
					}

				}

				if (!reservationByDate.isEmpty()) {

					return reservationByDate;
				} else {
					throw new ReservationException("No Reservation is available on this Date...");
				}

			} else {
				throw new ReservationException("No Reservation is available...!");
			}

		} else
			throw new ReservationException("Access Denied");

	}

	@Override
	public Integer getCurrentUserReservedBusId() throws UserException {
		CurrentUserSession find = sessionRepository.findAll().get(0);
		if (find != null) {
			Integer userId = find.getUserId();
			User user = userRepository.findById(userId).orElseThrow(() -> new UserException("You are Not LogggedIn!!"));
			Reservation reservation = user.getReservation();
			if (reservation==null) {
				throw new UserException("Please Book Some ticket First!!");
			}else {
				Bus bus = reservation.getBus();
                return bus.getBusId();
			}
		} else {
			throw new UserException("Please Login First!!");
		}

	}

}
