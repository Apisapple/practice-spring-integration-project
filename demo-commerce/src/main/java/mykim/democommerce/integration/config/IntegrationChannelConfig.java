package mykim.democommerce.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class IntegrationChannelConfig {

  // 주문 처리 관련 채널들
  @Bean
  public MessageChannel orderInputChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel orderValidationChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel stockCheckChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel paymentChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel shippingChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel orderCompletedChannel() {
    return new PublishSubscribeChannel();
  }

  // 상품 업데이트 관련 채널들
  @Bean
  public MessageChannel productFileInputChannel() {
    return new DirectChannel();
  }

  @Bean
  public MessageChannel productUpdateChannel() {
    return new DirectChannel();
  }

  // 이메일 알림 관련 채널들
  @Bean
  public MessageChannel emailChannel() {
    return new QueueChannel(100);
  }

  @Bean
  public MessageChannel notificationChannel() {
    return new PublishSubscribeChannel();
  }

  // 에러 처리 채널
  @Bean
  public MessageChannel errorChannel() {
    return new PublishSubscribeChannel();
  }
}
