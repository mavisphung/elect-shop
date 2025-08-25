package me.huypc.elect_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.huypc.elect_shop.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

}
