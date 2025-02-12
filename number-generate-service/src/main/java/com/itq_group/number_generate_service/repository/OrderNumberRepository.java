package com.itq_group.number_generate_service.repository;

import com.itq_group.number_generate_service.model.OrderNumber;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderNumberRepository extends MongoRepository<OrderNumber, String> {
}
