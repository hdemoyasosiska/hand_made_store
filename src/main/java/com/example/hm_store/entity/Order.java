package com.example.hm_store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "order_table")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id_of_order;

    @Column(name = "id_of_master")
    int id_of_master;



    @Column(name = "id_of_courier")
    int id_of_courier;

    @Column(name = "id_of_manager")
    int id_of_manager;

    @Getter
    @Column(name = "orderdate")
    Date orderDate= new Date();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Customer customer;

    @Column(name = "productid")
    private int[] productId;
    @Column(name = "total_price")
    private int total_price;



    public Order() {
    }

    public Order(Date orderDate) {
        this.orderDate = orderDate;
    }


}
