package mykim.democommerce.data.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mykim.democommerce.data.domain.Member;
import mykim.democommerce.data.domain.Product;
import mykim.democommerce.data.repository.MemberRepository;
import mykim.democommerce.data.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;

  @Override
  public void run(String... args) throws Exception {
    if (memberRepository.count() == 0) {
      initializeMembers();
    }

    if (productRepository.count() == 0) {
      initializeProducts();
    }
  }

  private void initializeMembers() {
    log.info("멤버 데이터 초기화 시작");

    Member member1 = Member.builder().memberId("user1").password("password1")
        .email("user1@example.com").name("김철수").build();

    Member member2 = Member.builder().memberId("user2").password("password2")
        .email("user2@example.com").name("이영희").build();

    Member member3 = Member.builder().memberId("user3").password("password3")
        .email("user3@example.com").name("박민수").build();

    memberRepository.save(member1);
    memberRepository.save(member2);
    memberRepository.save(member3);

    log.info("멤버 데이터 초기화 완료: {} 명", memberRepository.count());
  }

  private void initializeProducts() {
    log.info("상품 데이터 초기화 시작");

    Product product1 = Product.builder().name("노트북").price(1500000).stockQuantity(10).build();

    Product product2 = Product.builder().name("마우스").price(50000).stockQuantity(50).build();

    Product product3 = Product.builder().name("키보드").price(80000).stockQuantity(30).build();

    Product product4 = Product.builder().name("모니터").price(300000).stockQuantity(20).build();

    Product product5 = Product.builder().name("스피커").price(120000).stockQuantity(15).build();

    productRepository.save(product1);
    productRepository.save(product2);
    productRepository.save(product3);
    productRepository.save(product4);
    productRepository.save(product5);

    log.info("상품 데이터 초기화 완료: {} 개", productRepository.count());
  }
}
