package com.order.service;

import com.order.controller.OrdersApiDelegate;
import com.order.domain.entity.Order;
import com.order.domain.entity.OrderId;
import com.order.domain.entity.OrderItem;
import com.order.exception.OrderNotFoundException;
import com.order.model.OrderRequest;
import com.order.model.OrderResponse;
import com.order.repository.primary.OrderPrimaryRepository;
import com.order.repository.replica.OrderReplicaRepository;
import org.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OrderService implements OrdersApiDelegate {
    private final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderPrimaryRepository orderPrimaryRepository;
    private final OrderReplicaRepository orderReplicaRepository;

    public OrderService(OrderPrimaryRepository orderPrimaryRepository, OrderReplicaRepository orderReplicaRepository) {
        this.orderPrimaryRepository = orderPrimaryRepository;
        this.orderReplicaRepository = orderReplicaRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<OrderResponse> createOrder(OrderRequest orderRequest) {
        log.info("Creating order: {}", orderRequest);
        Order order =
                new Order(
                        orderRequest.getCustomerId(),
                        orderRequest.getItems()
                                .stream()
                                .map(
                                        orderItem -> toOrderItem(
                                                orderItem.getProductId(),
                                                orderItem.getQuantity()))
                                .toList(),
                        toBigDecimal(orderRequest.getTotalAmount()));
        Order orderSaved = orderPrimaryRepository.save(order);
        log.info("Order created: {}", orderSaved);
        return ResponseEntity.ok(toOrderResponse(orderSaved));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<OrderResponse> getOrderById(UUID orderId) {
        log.info("Getting order by id: {}", orderId);
        OrderId id = new OrderId(orderId);
        Order orderSearched =
                orderReplicaRepository
                        .findById(id)
                        .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return ResponseEntity.ok(toOrderResponse(orderSearched));
    }

    @Transactional(readOnly = true)
    private Order getOrderEntityById(UUID orderId) {
        OrderId id = new OrderId(orderId);
        return orderReplicaRepository
                .findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<OrderResponse>> getOrders() {
        log.info("Getting all orders");
        List<OrderResponse> orderResponseList = orderReplicaRepository.findAll()
                .stream()
                .map(this::toOrderResponse)
                .toList();
        return ResponseEntity.ok(orderResponseList);
    }

    @Transactional
    public void payOrder(OrderId orderId) {
        log.info("Paying order: {}", orderId);
        var order = getOrderEntityById(orderId.orderId());
        order.pay();
        orderPrimaryRepository.save(order);
    }

    @Transactional
    public void shipOrder(OrderId orderId) {
        log.info("Shipping order: {}", orderId);
        var order = getOrderEntityById(orderId.orderId());
        order.ship();
        orderPrimaryRepository.save(order);
    }

    @Transactional
    public void deliverOrder(OrderId orderId) {
        log.info("Delivering order: {}", orderId);
        var order = getOrderEntityById(orderId.orderId());
        order.deliver();
        orderPrimaryRepository.save(order);
    }

    @Transactional
    public void cancelOrder(OrderId orderId) {
        log.info("Canceling order: {}", orderId);
        var order = getOrderEntityById(orderId.orderId());
        order.cancel();
        orderPrimaryRepository.save(order);
    }

    public OrderItem toOrderItem(UUID productId, Integer quantity) {
        return new OrderItem(productId, quantity);
    }

    public OrderResponse toOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setCustomerId(order.getCustomerId());
        orderResponse.setItems(
                order.getOrderItems()
                        .stream()
                        .map(
                                orderItem -> {
                                    var orderItemResponse = new com.order.model.OrderItem();
                                    orderItemResponse.setProductId(orderItem.productId());
                                    orderItemResponse.setQuantity(orderItem.quantity());
                                    return orderItemResponse;
                                }
                        )
                        .toList()
        );
        orderResponse.setTotalAmount(order.getTotalAmount().toString());
        return orderResponse;
    }

    private BigDecimal toBigDecimal(String amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.of("pt", "BR")));
        try {
            return new BigDecimal(df.parse(amount).toString());
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing amount", e);
        }
    }

}
