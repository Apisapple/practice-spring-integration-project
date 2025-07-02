package mykim.democommerce.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailMessage {
    private String to;
    private String subject;
    private String body;
    private String type; // ORDER_CONFIRMATION, SHIPPING_NOTIFICATION, etc.
}
