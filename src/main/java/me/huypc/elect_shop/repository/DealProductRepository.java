package me.huypc.elect_shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.huypc.elect_shop.entity.DealProduct;

@Repository
public interface DealProductRepository extends JpaRepository<DealProduct, String> {

    List<DealProduct> findAllByDealIdAndProductIdIn(String dealId, List<String> productIds);
}
