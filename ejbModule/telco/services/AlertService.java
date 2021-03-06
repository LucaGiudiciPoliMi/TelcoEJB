package telco.services;

import java.sql.Timestamp;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import telco.entities.Alert;
import telco.entities.User;

@Stateless
public class AlertService {
	@PersistenceContext(unitName = "TelcoEJB")
	private EntityManager em;

	public AlertService() {
	}

	public Alert findById(int alertId) {
		return em.find(Alert.class, alertId);
	}

	public void deleteAlert(User user) {
		Alert a = findAlertByUser(user);
		if (a != null) {
			em.remove(a);
			em.flush();
		}
	}

	public void handleAlert(User user, int totalAmount, String typeOfPayment) {
		Alert a = findAlertByUser(user);
		Timestamp lastRejection = new Timestamp(System.currentTimeMillis());
		if (a == null) {
			a = createAlert(user, lastRejection, totalAmount);
		} else {
			if (typeOfPayment.equals("Failed payment"))
				a.setLastRejection(lastRejection);
			a.setAmount(totalAmount);
		}
		em.persist(a);
		em.flush();

	}

	public Alert createAlert(User user, Timestamp lastRejection, int totalAmount) {
		Alert a = new Alert();
		a.setUser(user);
		a.setLastRejection(lastRejection);
		a.setAmount(totalAmount);
		return a;
	}

	public List<Alert> findAllAlerts() {
		return em.createNamedQuery("Alert.findAll", Alert.class).getResultList();
	}

	// The exception is catch here to handle the null case in which there are no
	// alerts for the user.
	public Alert findAlertByUser(User user) {
		try {
			return em.createNamedQuery("Alert.findByUser", Alert.class).setParameter(1, user).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
