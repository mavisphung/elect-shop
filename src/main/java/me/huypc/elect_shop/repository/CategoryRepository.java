package me.huypc.elect_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.huypc.elect_shop.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    // Add any custom query methods if needed
}
