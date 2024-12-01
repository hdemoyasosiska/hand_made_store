package com.example.hm_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "persistentsession")
@Getter
@Setter
public class PersistentSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "series")
    private String series;
    @Column(name = "username")
    private String userName;
    @Column(name = "token")
    private String token;
    @Column(name = "last_used")
    @UpdateTimestamp
    private LocalDateTime lastUsed;

}