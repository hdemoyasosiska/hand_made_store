package com.example.hm_store.repo;

import com.example.hm_store.entity.IndividualOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface IndividualOrderRepository extends JpaRepository<IndividualOrder, Integer>   {
}
