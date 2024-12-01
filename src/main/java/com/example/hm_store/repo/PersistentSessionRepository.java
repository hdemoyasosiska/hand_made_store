package com.example.hm_store.repo;

import com.example.hm_store.entity.PersistentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersistentSessionRepository extends JpaRepository<PersistentSession, Integer> {
}
