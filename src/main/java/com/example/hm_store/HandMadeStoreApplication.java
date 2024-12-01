package com.example.hm_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.SQLException;
import java.text.ParseException;


@SpringBootApplication
public class HandMadeStoreApplication {

    public static void main(String[] args) throws SQLException, ParseException {
        SpringApplication.run(HandMadeStoreApplication.class, args);
    }

}
