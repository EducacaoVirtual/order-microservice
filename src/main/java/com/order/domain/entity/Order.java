package com.order.domain.entity;

import com.order.domain.enums.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "orders")
public class Order {

    @EmbeddedId
    private OrderId orderId;

    @Column(nullable = false)
    private UUID customerId;

    @ElementCollection
    @CollectionTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id")
    )
    private List<OrderItem> orderItems;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order() {
    }

    public Order(UUID customerId, List<OrderItem> orderItems, BigDecimal totalAmount) {
        this.orderId = new OrderId(UUID.randomUUID());
        this.customerId = customerId;
        this.orderItems = Objects.requireNonNull(orderItems);
        this.totalAmount = totalAmount;
        validate(customerId, totalAmount);
        this.status = OrderStatus.PENDING;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    private void validate(UUID customerId, BigDecimal amount) {
        StringBuilder validation = new StringBuilder();
        if (customerId == null) {
            validation.append("customerId cannot be null");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            validation.append("Total amount must be greater than zero");
        }

        if (!validation.isEmpty()) {
            throw new IllegalArgumentException(validation.toString());
        }
    }

    public void pay() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be paid.");
        }
        this.status = OrderStatus.PAID;
    }

    public void ship() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be shipped.");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be delivered.");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivered orders cannot be canceled.");
        }
        this.status = OrderStatus.CANCELED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
