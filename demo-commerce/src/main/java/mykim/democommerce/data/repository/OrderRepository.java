package mykim.democommerce.data.repository;

import mykim.democommerce.data.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByMemberIdOrderByOrderDateDesc(Long memberId);

  List<Order> findByStatus(Order.OrderStatus status);
}
