package mykim.democommerce.integration.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mykim.democommerce.data.domain.Product;
import mykim.democommerce.data.repository.ProductRepository;
import mykim.democommerce.integration.dto.ProductUpdateMessage;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductUpdateFlow {

  private final ProductRepository productRepository;

  @Transformer(inputChannel = "productFileInputChannel", outputChannel = "productUpdateChannel")
  public List<ProductUpdateMessage> parseProductFile(@Payload File file,
      @Header(FileHeaders.FILENAME) String filename) {
    log.info("상품 파일 처리 시작: {}", filename);

    try {
      List<String> lines = Files.readAllLines(file.toPath());
      return lines.stream().skip(1) // 헤더 행 스킵
          .map(this::parseProductLine).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("상품 파일 파싱 오류: {}", filename, e);
      throw new RuntimeException("파일 파싱 실패", e);
    }
  }

  @ServiceActivator(inputChannel = "productUpdateChannel")
  public void updateProduct(@Payload ProductUpdateMessage message) {
    log.info("상품 업데이트: {} - {}", message.getOperation(), message.getProductName());

    try {
      switch (message.getOperation().toUpperCase()) {
        case "CREATE":
          createProduct(message);
          break;
        case "UPDATE":
          updateExistingProduct(message);
          break;
        case "DELETE":
          deleteProduct(message);
          break;
        default:
          log.warn("알 수 없는 작업: {}", message.getOperation());
      }
    } catch (Exception e) {
      log.error("상품 업데이트 실패: {}", message, e);
      throw e;
    }
  }

  private ProductUpdateMessage parseProductLine(String line) {
    String[] parts = line.split(",");
    if (parts.length < 5) {
      throw new RuntimeException("잘못된 CSV 형식: " + line);
    }

    return ProductUpdateMessage.builder()
        .productId(parts[0].trim().isEmpty() ? null : Long.parseLong(parts[0].trim()))
        .productName(parts[1].trim()).price(Integer.parseInt(parts[2].trim()))
        .stockQuantity(Integer.parseInt(parts[3].trim())).operation(parts[4].trim()).build();
  }

  private void createProduct(ProductUpdateMessage message) {
    Product product = Product.builder().name(message.getProductName()).price(message.getPrice())
        .stockQuantity(message.getStockQuantity()).build();

    productRepository.save(product);
    log.info("새 상품 생성: {}", message.getProductName());
  }

  private void updateExistingProduct(ProductUpdateMessage message) {
    if (message.getProductId() == null) {
      throw new RuntimeException("상품 ID가 필요합니다: " + message.getProductName());
    }

    Product product = productRepository.findById(message.getProductId())
        .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + message.getProductId()));

    // 새로운 Product 객체 생성 (불변성 유지)
    Product updatedProduct = Product.builder().id(product.getId()).name(message.getProductName())
        .price(message.getPrice()).stockQuantity(message.getStockQuantity()).build();

    productRepository.save(updatedProduct);
    log.info("상품 업데이트: {}", message.getProductName());
  }

  private void deleteProduct(ProductUpdateMessage message) {
    if (message.getProductId() == null) {
      throw new RuntimeException("상품 ID가 필요합니다: " + message.getProductName());
    }

    productRepository.deleteById(message.getProductId());
    log.info("상품 삭제: {}", message.getProductName());
  }
}
