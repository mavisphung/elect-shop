package me.huypc.elect_shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.huypc.elect_shop.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT p FROM Product p WHERE " +
           "(:searchText IS NULL OR :searchText = '' OR " +
           "p.name LIKE CONCAT('%', :searchText, '%'))")
    Page<Product> findAllWithSearch(@Param("searchText") String searchText, Pageable pageable);

}
