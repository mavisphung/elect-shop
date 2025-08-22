package me.huypc.elect_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.huypc.elect_shop.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

}
