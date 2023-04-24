package daos;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import entities.Order;
import entities.User;

/**
 * Class that makes the database communication layer role in relation with of the orders table.
 * 
 * @author Wanderley Drumond
 */
@Stateless
public class OrderDAO extends GenericDAO<Order> {

	/**
	 * <p>The serial version identifier for this class.<p>
	 * <p>This identifier is used during deserialisation to verify that the sender and receiver of a serialised object have loaded classes for that object that are compatible with respect to serialisation.<p>
	 */
	private static final long serialVersionUID = 1L;

	public OrderDAO() {
		super(Order.class);
	}

	/**
	 * Finds an order by its id in the database.
	 * 
	 * @param orderId primary key that identifies the order to find
	 * @return If:
	 * 		<ul>
	 * 			<li>Finds, {@link Optional} {@link Order} corresponding </li>
	 * 			<li>Does not find, {@link Optional} empty</li>
	 * 			<li>Something goes wrong with the database, null</li>
	 * 		</ul>
	 */
	public Optional<Order> findById(Short orderId) {
		try {
			final CriteriaQuery<Order> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Order.class);
			Root<Order> orderTable = CRITERIA_QUERY.from(Order.class);
			
			CRITERIA_QUERY.select(orderTable).where(criteriaBuilder.equal(orderTable.get("id"), orderId));
			
			return Optional.ofNullable(entityManager.createQuery(CRITERIA_QUERY).getSingleResult());
		} catch (NoResultException noResultException) {
			Logger.getLogger(OrderDAO.class.getName()).log(Level.FINE, "in findById() in OrderDAO", noResultException);
			
			return Optional.empty();
		} catch (Exception exception) {
			Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "in findById() in OrderDAO", exception);
			
			return null;
		} 
	}

	/**
	 * Finds the list of orders from the logged user which has the isConcluded column marked as true
	 * 
	 * @param token logged user identifier key
	 * @return If:
	 * 		<ul>
	 * 			<li>Finds, {@link Order} {@link List} corresponding</li>
	 * 			<li>Something goes wrong with the database, null</li>
	 * 		</ul>
	 */
	public List<Order> findAllConcluded(UUID token) {
		try {
			final CriteriaQuery<Order> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Order.class);
			Root<Order> orderTable = CRITERIA_QUERY.from(Order.class);
			Join<Order, User> userTable = orderTable.join("buyer");
			
			CRITERIA_QUERY.select(orderTable).where(criteriaBuilder.and(
					criteriaBuilder.equal(userTable.get("token"), token), 
					criteriaBuilder.equal(orderTable.get("isConcluded"), true)));
			
			return entityManager.createQuery(CRITERIA_QUERY).getResultList();
		} catch (Exception exception) {
			Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "in findAllConcluded() in OrderDAO", exception);
			
			return null;
		}
	}

	/**
	 * Finds a non concluded order that belongs to the logged user.
	 * 
	 * @param token logged user identifier key
	 * @return If:
	 * 		<ul>
	 * 			<li>Finds, {@link Optional} {@link Order} corresponding </li>
	 * 			<li>Does not find, {@link Optional} empty</li>
	 * 			<li>Something goes wrong with the database, null</li>
	 * 		</ul>
	 */
	public Optional<Order> findNonConcludedOrder(UUID token) {
		try {
			final CriteriaQuery<Order> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Order.class);
			Root<Order> orderTable = CRITERIA_QUERY.from(Order.class);
			Join<Order, User> userTable = orderTable.join("buyer");
			
			CRITERIA_QUERY.select(orderTable).where(criteriaBuilder.and(
					criteriaBuilder.equal(orderTable.get("isConcluded"), false), 
					criteriaBuilder.equal(userTable.get("token"), token)));
			
			return Optional.ofNullable(entityManager.createQuery(CRITERIA_QUERY).getSingleResult());
		} catch (NoResultException noResultException) {
			Logger.getLogger(OrderDAO.class.getName()).log(Level.FINE, "in findNonConcludedOrder()", noResultException);
			
			return Optional.empty();
		} catch (Exception exception) {
			Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "in findNonConcludedOrder()", exception);
			
			return null;
		}
	}

	public Short countAllNonConcluded() {
		try {
			final CriteriaQuery<Order> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Order.class);
			Root<Order> orderTable = CRITERIA_QUERY.from(Order.class);
			
			CRITERIA_QUERY.select(orderTable).where(criteriaBuilder.equal(orderTable.get("isConcluded"), false));
			
			return (short) entityManager.createQuery(CRITERIA_QUERY).getResultList().size();
		} catch (Exception exception) {
			Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "in countAllNonConcluded()", exception);
			
			return null;
		}
	}

	/**
	 * Sums the total value from all orders.
	 * 
	 * @return the sum of total value from all orders existent in database
	 */
	public Float sumTotalValue() {
		try {
			final CriteriaQuery<Float> CRITERIA_QUERY;
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CRITERIA_QUERY = criteriaBuilder.createQuery(Float.class);
			Root<Order> orderTable = CRITERIA_QUERY.from(Order.class);
			
			CRITERIA_QUERY.multiselect(criteriaBuilder.sum(orderTable.<BigDecimal>get("totalValue"))).where(criteriaBuilder.equal(orderTable.get("isConcluded"), true));
			
			return entityManager.createQuery(CRITERIA_QUERY).getSingleResult();
		} catch (NoResultException noResultException) {
			Logger.getLogger(OrderDAO.class.getName()).log(Level.FINE, "in sumTotalValue()", noResultException);
			return 0F;
		} catch (Exception exception) {
			Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "in sumTotalValue()", exception);
			return null;
		}
	}

	/**
	 * Sums the total value from all orders from current month.
	 * 
	 * @return the sum of the total value from all orders from current month
	 */
	public Float sumTotalValueConcludedOrdersCurrentMonth() {
		final CriteriaQuery<Float> CRITERIA_QUERY;
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CRITERIA_QUERY = criteriaBuilder.createQuery(Float.class);
		Root<Order> orderTable = CRITERIA_QUERY.from(Order.class);
		
		Timestamp firstDayOfMonth = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)).with(TemporalAdjusters.firstDayOfMonth()));
		Timestamp lastDayOfMonth = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59)).with(TemporalAdjusters.lastDayOfMonth()));
		
		Predicate currentMonth = criteriaBuilder.between(orderTable.get("lastUpdate"), firstDayOfMonth, lastDayOfMonth);
	
		CRITERIA_QUERY.multiselect(criteriaBuilder.sum(
				orderTable.<BigDecimal>get("totalValue"))).where(
						criteriaBuilder.and(
								criteriaBuilder.equal(orderTable.get("isConcluded"), true), 
								currentMonth));
		return entityManager.createQuery(CRITERIA_QUERY).getSingleResult();
	}

	/**
	 * Sums the total value from all orders from last month.
	 * 
	 * @return the sum of the total value from all orders from last month month
	 */
	public Float sumTotalValueConcludedOrdersLastMonth() {
		final CriteriaQuery<Float> CRITERIA_QUERY;
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CRITERIA_QUERY = criteriaBuilder.createQuery(Float.class);
		Root<Order> orderTable = CRITERIA_QUERY.from(Order.class);
		
		Timestamp firstDayOfMonth = Timestamp.valueOf(LocalDateTime.of(LocalDate.now().minusMonths(1L), LocalTime.of(0, 0, 0)).with(TemporalAdjusters.firstDayOfMonth()));
		Timestamp lastDayOfMonth = Timestamp.valueOf(LocalDateTime.of(LocalDate.now().minusMonths(1L), LocalTime.of(23, 59, 59)).with(TemporalAdjusters.lastDayOfMonth()));
		
		Predicate currentMonth = criteriaBuilder.between(orderTable.get("lastUpdate"), firstDayOfMonth, lastDayOfMonth);
	
		CRITERIA_QUERY.multiselect(criteriaBuilder.sum(
				orderTable.<BigDecimal>get("totalValue"))).where(
						criteriaBuilder.and(
								criteriaBuilder.equal(orderTable.get("isConcluded"), true), 
								currentMonth));

		return entityManager.createQuery(CRITERIA_QUERY).getSingleResult();
	}
}