package mykim.democommerce.data.repository;

import mykim.democommerce.data.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByNameContaining(String name);

  List<Product> findByStockQuantityGreaterThan(Integer stockQuantity);
}
