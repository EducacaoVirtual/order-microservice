package com.order.repository.replica;

import com.order.domain.entity.Order;
import com.order.domain.entity.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional("replicaTransactionManager")
public interface OrderReplicaRepository extends JpaRepository<Order, OrderId> {
}
