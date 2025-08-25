package me.huypc.elect_shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import me.huypc.elect_shop.entity.DealProduct;

@Repository
public interface DealProductRepository extends JpaRepository<DealProduct, String> {

    List<DealProduct> findAllByDealIdAndProductIdIn(String dealId, List<String> productIds);

    Optional<DealProduct> findByProductId(String productId);

    List<DealProduct> findAllByProductId(String productId);

    @Query("SELECT dp FROM DealProduct dp JOIN FETCH dp.deal WHERE dp.product.id IN :productIds")
    List<DealProduct> findAllByProductIdIn(List<String> productIds);
}
