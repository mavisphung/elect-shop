package me.huypc.elect_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.huypc.elect_shop.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

}
