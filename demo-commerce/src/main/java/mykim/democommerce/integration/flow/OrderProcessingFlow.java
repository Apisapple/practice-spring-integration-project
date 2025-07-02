package mykim.democommerce.integration.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mykim.democommerce.data.domain.Member;
import mykim.democommerce.data.domain.Order;
import mykim.democommerce.data.domain.OrderItem;
import mykim.democommerce.data.domain.Product;
import mykim.democommerce.data.repository.MemberRepository;
import mykim.democommerce.data.repository.OrderRepository;
import mykim.democommerce.data.repository.ProductRepository;
import mykim.democommerce.integration.dto.EmailMessage;
import mykim.democommerce.integration.dto.OrderMessage;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingFlow {

  private final OrderRepository orderRepository;
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;

  @ServiceActivator(inputChannel = "orderInputChannel", outputChannel = "orderValidationChannel")
  public OrderMessage receiveOrder(@Payload OrderMessage orderMessage) {
    log.info("주문 접수: {}", orderMessage.getOrderId());

    // 주문을 데이터베이스에 저장
    Order order = createOrderFromMessage(orderMessage);
    Order savedOrder = orderRepository.save(order);

    orderMessage.setOrderId(savedOrder.getId());
    orderMessage.setStatus("PENDING");
    orderMessage.setOrderDate(LocalDateTime.now());

    return orderMessage;
  }

  @ServiceActivator(inputChannel = "orderValidationChannel", outputChannel = "stockCheckChannel")
  public OrderMessage validateOrder(@Payload OrderMessage orderMessage) {
    log.info("주문 검증: {}", orderMessage.getOrderId());

    // 회원 정보 확인
    Member member = memberRepository.findById(orderMessage.getMemberId())
        .orElseThrow(() -> new RuntimeException("Member not found: " + orderMessage.getMemberId()));

    orderMessage.setMemberEmail(member.getEmail());
    orderMessage.setMemberName(member.getName());

    // 상품 정보 확인
    for (OrderMessage.OrderItemMessage item : orderMessage.getItems()) {
      Product product = productRepository.findById(item.getProductId())
          .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
      item.setProductName(product.getName());
      item.setPrice(product.getPrice());
    }

    return orderMessage;
  }

  @ServiceActivator(inputChannel = "stockCheckChannel", outputChannel = "paymentChannel")
  public OrderMessage checkStock(@Payload OrderMessage orderMessage) {
    log.info("재고 확인: {}", orderMessage.getOrderId());

    for (OrderMessage.OrderItemMessage item : orderMessage.getItems()) {
      Product product = productRepository.findById(item.getProductId())
          .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

      if (product.getStockQuantity() < item.getQuantity()) {
        throw new RuntimeException("재고 부족: " + product.getName() + " (요청: " + item.getQuantity()
            + ", 재고: " + product.getStockQuantity() + ")");
      }

      // 재고 차감
      product.subStock(item.getQuantity());
      productRepository.save(product);
    }

    return orderMessage;
  }

  @ServiceActivator(inputChannel = "paymentChannel", outputChannel = "shippingChannel")
  public OrderMessage processPayment(@Payload OrderMessage orderMessage) {
    log.info("결제 처리: {}", orderMessage.getOrderId());

    // 결제 로직 시뮬레이션
    try {
      Thread.sleep(1000); // 결제 API 호출 시뮬레이션

      // 주문 상태 업데이트
      Order order = orderRepository.findById(orderMessage.getOrderId())
          .orElseThrow(() -> new RuntimeException("Order not found"));
      order.changeStatus(Order.OrderStatus.PAYMENT_COMPLETED);
      orderRepository.save(order);

      orderMessage.setStatus("PAYMENT_COMPLETED");

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("결제 처리 중 오류 발생", e);
    }

    return orderMessage;
  }

  @ServiceActivator(inputChannel = "shippingChannel", outputChannel = "orderCompletedChannel")
  public OrderMessage prepareShipping(@Payload OrderMessage orderMessage) {
    log.info("배송 준비: {}", orderMessage.getOrderId());

    // 배송 준비 로직
    Order order = orderRepository.findById(orderMessage.getOrderId())
        .orElseThrow(() -> new RuntimeException("Order not found"));
    order.changeStatus(Order.OrderStatus.SHIPPED);
    orderRepository.save(order);

    orderMessage.setStatus("SHIPPED");

    return orderMessage;
  }

  @ServiceActivator(inputChannel = "orderCompletedChannel", outputChannel = "notificationChannel")
  @Transformer
  public EmailMessage createOrderCompletionEmail(@Payload OrderMessage orderMessage) {
    log.info("주문 완료 이메일 생성: {}", orderMessage.getOrderId());

    String emailBody =
        String.format(
            "안녕하세요 %s님,\n\n" + "주문번호 %d가 성공적으로 처리되었습니다.\n" + "총 금액: %d원\n" + "배송 주소: %s\n\n"
                + "감사합니다.",
            orderMessage.getMemberName(), orderMessage.getOrderId(), orderMessage.getTotalAmount(),
            orderMessage.getDeliveryAddress());

    return EmailMessage.builder().to(orderMessage.getMemberEmail())
        .subject("[주문완료] 주문번호 " + orderMessage.getOrderId()).body(emailBody)
        .type("ORDER_COMPLETION").build();
  }

  private Order createOrderFromMessage(OrderMessage orderMessage) {
    Member member = memberRepository.findById(orderMessage.getMemberId())
        .orElseThrow(() -> new RuntimeException("Member not found"));

    List<OrderItem> orderItems = orderMessage.getItems().stream().map(item -> {
      Product product = productRepository.findById(item.getProductId())
          .orElseThrow(() -> new RuntimeException("Product not found"));
      return OrderItem.builder().product(product).quantity(item.getQuantity())
          .price(product.getPrice()).build();
    }).collect(Collectors.toList());

    Order order = Order.builder().member(member).status(Order.OrderStatus.PENDING)
        .orderDate(LocalDateTime.now()).deliveryAddress(orderMessage.getDeliveryAddress())
        .totalAmount(orderMessage.getTotalAmount()).build();

    orderItems.forEach(order::addOrderItem);

    return order;
  }
}
