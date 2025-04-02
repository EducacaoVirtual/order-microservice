package com.order.domain.entity;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record OrderItem(
        UUID productId,
        Integer quantity
) {
}
