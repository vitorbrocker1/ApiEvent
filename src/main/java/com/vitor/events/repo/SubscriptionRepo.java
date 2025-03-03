package com.vitor.events.repo;

import com.vitor.events.dto.SubscriptionRankingItem;
import com.vitor.events.model.Event;
import com.vitor.events.model.Subscription;
import com.vitor.events.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SubscriptionRepo extends CrudRepository<Subscription, Integer> {
    Subscription findByEventAndUser(Event evt, User user);

    @Query(value = "SELECT COUNT(s.subscription_number) AS quantidade, " +
            "s.indication_user_id, u.user_name " +
            "FROM tbl_subscription s " +
            "INNER JOIN tbl_user u ON s.indication_user_id = u.user_id " +
            "WHERE s.indication_user_id IS NOT NULL " +
            "AND s.event_id = :eventId " +
            "GROUP BY s.indication_user_id, u.user_name " +
            "ORDER BY quantidade DESC",
            nativeQuery = true)
    List<SubscriptionRankingItem> generateRanking(@Param("eventId") Integer eventId);


}
