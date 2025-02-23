package com.vitor.events.repo;

import com.vitor.events.model.Subscription;
import org.springframework.data.repository.CrudRepository;



public interface subscriptionRepo extends CrudRepository<Subscription, Integer> {
}
