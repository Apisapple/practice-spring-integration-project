package mykim.democommerce.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mykim.democommerce.data.domain.Order;
import mykim.democommerce.data.repository.OrderRepository;
import mykim.democommerce.integration.dto.OrderMessage;
import mykim.democommerce.integration.gateway.OrderGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderGateway orderGateway;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderMessage orderMessage) {
        try {
            log.info("주문 생성 요청 수신: {}", orderMessage);
            
            // Spring Integration을 통한 비동기 주문 처리
            orderGateway.processOrder(orderMessage);
            
            return ResponseEntity.ok("주문이 접수되었습니다. 처리 중입니다.");
            
        } catch (Exception e) {
            log.error("주문 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body("주문 처리 실패: " + e.getMessage());
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<OrderMessage> createOrderSync(@RequestBody OrderMessage orderMessage) {
        try {
            log.info("동기 주문 생성 요청 수신: {}", orderMessage);
            
            // Spring Integration을 통한 동기 주문 처리
            OrderMessage result = orderGateway.processOrderWithReply(orderMessage);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("동기 주문 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(order -> ResponseEntity.ok(order))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Order>> getOrdersByMember(@PathVariable Long memberId) {
        List<Order> orders = orderRepository.findByMemberIdOrderByOrderDateDesc(memberId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderRepository.findByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
