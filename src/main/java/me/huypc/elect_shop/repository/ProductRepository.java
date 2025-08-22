package me.huypc.elect_shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import me.huypc.elect_shop.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT p FROM Product p JOIN FETCH p.inventory WHERE " +
           "(:searchText IS NULL OR " +
           "p.name LIKE CONCAT('%', :searchText, '%'))")
    Page<Product> findAllWithSearch(String searchText, Pageable pageable);

    @Query("""
            SELECT p FROM Product p JOIN FETCH p.inventory
            WHERE (:searchText IS NULL OR p.name LIKE CONCAT('%', :searchText, '%'))
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:minPrice IS NULL OR p.unitPrice >= :minPrice)
            AND (:maxPrice IS NULL OR p.unitPrice <= :maxPrice)
            AND (:isAvailable IS NULL OR CASE WHEN :isAvailable = true THEN p.inventory.onHand - p.inventory.reserved > 0 END)
            """)
    Page<Product> findAllByFilter(String searchText, String categoryId, Double minPrice, Double maxPrice, Boolean isAvailable, Pageable pageable);
}
