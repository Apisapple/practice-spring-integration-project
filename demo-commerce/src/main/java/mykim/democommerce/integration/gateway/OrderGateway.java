package mykim.democommerce.integration.gateway;

import mykim.democommerce.integration.dto.OrderMessage;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface OrderGateway {

  @Gateway(requestChannel = "orderInputChannel")
  void processOrder(OrderMessage orderMessage);

  @Gateway(requestChannel = "orderInputChannel", replyTimeout = 10000)
  OrderMessage processOrderWithReply(OrderMessage orderMessage);
}
