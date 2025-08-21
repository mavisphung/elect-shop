package me.huypc.elect_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.huypc.elect_shop.entity.Deal;

@Repository
public interface DealRepository extends JpaRepository<Deal, String> {

}
