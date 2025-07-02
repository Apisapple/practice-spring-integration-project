package mykim.democommerce.integration.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mykim.democommerce.integration.dto.EmailMessage;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationFlow {

  @ServiceActivator(inputChannel = "notificationChannel", outputChannel = "emailChannel")
  public EmailMessage processNotification(@Payload EmailMessage emailMessage) {
    log.info("알림 처리: {} -> {}", emailMessage.getType(), emailMessage.getTo());

    // 이메일 메시지 전처리 (템플릿 적용, 개인화 등)
    EmailMessage processedMessage = EmailMessage.builder().to(emailMessage.getTo())
        .subject("[Demo Commerce] " + emailMessage.getSubject())
        .body(addEmailFooter(emailMessage.getBody())).type(emailMessage.getType()).build();

    return processedMessage;
  }

  @ServiceActivator(inputChannel = "emailChannel")
  public void sendEmail(@Payload EmailMessage emailMessage) {
    log.info("이메일 발송: {}", emailMessage.getTo());

    // 실제 환경에서는 JavaMailSender를 사용하여 이메일 발송
    // 여기서는 로그로 시뮬레이션
    try {
      Thread.sleep(500); // 이메일 발송 시뮬레이션

      log.info("=== 이메일 발송 완료 ===");
      log.info("수신자: {}", emailMessage.getTo());
      log.info("제목: {}", emailMessage.getSubject());
      log.info("내용:\n{}", emailMessage.getBody());
      log.info("========================");

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("이메일 발송 중 오류 발생", e);
      throw new RuntimeException("이메일 발송 실패", e);
    }
  }

  private String addEmailFooter(String body) {
    return body + "\n\n" + "---\n" + "Demo Commerce\n" + "본 메일은 발신전용입니다.\n" + "문의사항은 고객센터로 연락해주세요.";
  }
}
