package com.example.hm_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "individual_order_table")
public class IndividualOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id_of_order;

    @Column(name = "id_of_master")
    int id_of_master;


    @Getter
    @Column(name = "orderdate")
    Date orderDate= new Date();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Customer customer;

    @Column(name = "requirements")
    private String requirements;

    @Column(name = "budget")
    private int budget;


    @Column(name = "color")
    private String color;

    @Column(name = "materials")
    private String materials;

    @Column(name = "deadline")
    private Date deadline;

    @OneToMany(mappedBy = "individualOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderFile> files = new ArrayList<>();





    public IndividualOrder() {
    }

    public IndividualOrder(Date orderDate) {
        this.orderDate = orderDate;
    }


}
