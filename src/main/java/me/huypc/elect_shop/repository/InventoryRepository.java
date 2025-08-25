package me.huypc.elect_shop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import me.huypc.elect_shop.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    // allow other transaction read but not update except the current ones
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i FROM Inventory i WHERE i.productId = :productId
            """)
    Optional<Inventory> findByProductIdForUpdate(String productId);

}
