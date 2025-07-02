package mykim.democommerce.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderMessage {
    private Long orderId;
    private Long memberId;
    private String memberEmail;
    private String memberName;
    private List<OrderItemMessage> items;
    private String deliveryAddress;
    private Integer totalAmount;
    private String status;
    private LocalDateTime orderDate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemMessage {
        private Long productId;
        private String productName;
        private Integer quantity;
        private Integer price;
    }
}
