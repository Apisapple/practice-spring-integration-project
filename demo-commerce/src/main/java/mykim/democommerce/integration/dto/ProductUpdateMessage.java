package mykim.democommerce.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateMessage {
    private Long productId;
    private String productName;
    private Integer price;
    private Integer stockQuantity;
    private String operation; // CREATE, UPDATE, DELETE
}
