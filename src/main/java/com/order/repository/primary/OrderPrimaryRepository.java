package com.order.repository.primary;

import com.order.domain.entity.Order;
import com.order.domain.entity.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional("mainTransactionManager")
public interface OrderPrimaryRepository extends JpaRepository<Order, OrderId> {
}
