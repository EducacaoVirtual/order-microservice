package com.order.domain.entity;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record OrderId(UUID orderId) {
    public OrderId {
        if (orderId == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
    }

    public OrderId() {
        this(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return orderId.toString();
    }

}
