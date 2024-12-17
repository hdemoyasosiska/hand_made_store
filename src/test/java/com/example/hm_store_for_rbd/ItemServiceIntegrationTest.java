package com.example.hm_store_for_rbd;


import com.example.hm_store.HandMadeStoreApplication;
import com.example.hm_store.Services.ItemService;
import com.example.hm_store.entity.Item;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasSize;


@SpringBootTest(classes = HandMadeStoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

//@Sql({
//        "classpath:sql/backup_021224.sql"
//})
@ActiveProfiles("test")
@Testcontainers

public class ItemServiceIntegrationTest {
    @LocalServerPort
    private Integer port;
    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15")
            .withUsername("postgres")
            .withPassword("qweasd")
            .withDatabaseName("HandMadeStore")
            .withInitScript("init.sql");

    @Autowired
    private ItemService itemService;



    @BeforeAll
    static void runContainer(){
        Startables.deepStart(container);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        System.out.println(port);
    }


    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void getById(){
        Item item = itemService.getById(1);
        System.out.println(item.getId() + item.getName());
        Assertions.assertNotNull(item);
    }

    @Test
    void shouldGetAllTodos() {
//        Item item = new Item();
//        //item.setId(777);
//        item.setName("name");
//        item.setMaterial("material");
//        item.setSize("20");
//        item.setImage("image");
//        item.setColor("color");
//        item.setQuantity(2);
//        item.setPrice(200);
//        itemService.saveItem(item);


                when()
                .get("/items/1")
                .then()
                        .log().all()
                .statusCode(200);

    }
}
