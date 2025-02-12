package com.itq_group.orders_service.repository;

import com.itq_group.orders_service.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.orderDate = :orderDate AND o.totalAmount >= :totalAmount")
    Page<Order> findOrdersByDateAndTotalAmount(
            @Param("orderDate") Date orderDate,
            @Param("totalAmount") BigDecimal totalAmount,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "AND NOT EXISTS (SELECT 1 FROM o.orderDetails od WHERE od.itemCode = :itemCode)")
    Page<Order> findOrdersNotContainingProductAndInDateRange(
            @Param("itemCode") Long itemCode,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);
}
