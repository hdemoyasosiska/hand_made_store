package com.example.hm_store_for_rbd.Controllers;

import com.example.hm_store.Controllers.HTMLController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)

public class HTMLControllerTest {
    @InjectMocks
    HTMLController htmlController;

    private MockMvc mockMvc;

    @Mock
    private ResourceLoader resourceLoader;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(htmlController).build();
    }

    @Test
    void getHomePage() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home.html"));
    }

    @Test
    void getRegistPage() throws Exception {
        mockMvc.perform(get("/regist"))
                .andExpect(status().isOk())
                .andExpect(view().name("regist.html"));
    }

    @Test
    void getCatalogPage() throws Exception {
        mockMvc.perform(get("/catalog"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog.html"));
    }

    @Test
    void getContentPage() throws Exception {
        mockMvc.perform(get("/content"))
                .andExpect(status().isOk())
                .andExpect(view().name("content.html"));
    }

    @Test
    void getAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about.html"));
    }
    @Test
    void getAddItemPage() throws Exception {
        mockMvc.perform(get("/items/add_item"))
                .andExpect(status().isOk())
                .andExpect(view().name("add_item.html"));
    }

    @Test
    void getBasketPage() throws Exception {
        mockMvc.perform(get("/basket"))
                .andExpect(status().isOk())
                .andExpect(view().name("basket.html"));
    }

    @Test
    void getIndividualOrderPage() throws Exception {
        mockMvc.perform(get("/individual_order"))
                .andExpect(status().isOk())
                .andExpect(view().name("individual_order.html"));
    }

    @Test
    void getCSS_WithResourceLoader_ShouldReturnCSSFileContents() throws Exception {
        String fileName = "style.css";
        String fileContent = "body { background-color: white; }";

        // Мокируем ResourceLoader и Resource
        Resource mockResource = mock(Resource.class);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes()));

        when(resourceLoader.getResource("classpath:/static/css/" + fileName)).thenReturn(mockResource);

        mockMvc.perform(get("/static/css/" + fileName))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent.getBytes()));
    }

    @Test
    void getImage_ShouldReturnImageBytes() throws Exception {
        // Имя тестового изображения и его содержимое
        String fileName = "test.jpg";
        byte[] imageBytes = {1, 2, 3, 4, 5}; // Пример содержимого изображения

        // Мокируем Resource и ResourceLoader
        Resource mockResource = mock(Resource.class);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(imageBytes));
        when(resourceLoader.getResource("classpath:/static/img/" + fileName)).thenReturn(mockResource);

        // Выполняем запрос и проверяем результат
        mockMvc.perform(get("/static/img/" + fileName))
                .andExpect(status().isOk()) // Проверяем, что статус ответа 200 OK
                .andExpect(content().bytes(imageBytes)) // Проверяем содержимое ответа
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)); // Проверяем заголовок Content-Type
    }
    @Test
    void getJS_ShouldReturnImageBytes() throws Exception {
        // Имя тестового изображения и его содержимое
        String fileName = "test.js";
        byte[] jsBytes = {1, 2, 3, 4, 5}; // Пример содержимого изображения

        // Мокируем Resource и ResourceLoader
        Resource mockResource = mock(Resource.class);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(jsBytes));
        when(resourceLoader.getResource("classpath:/static/js/" + fileName)).thenReturn(mockResource);

        // Выполняем запрос и проверяем результат
        mockMvc.perform(get("/static/js/" + fileName))
                .andExpect(status().isOk()) // Проверяем, что статус ответа 200 OK
                .andExpect(content().bytes(jsBytes)) // Проверяем содержимое ответа
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,
                        "application/javascript")); // Проверяем заголовок Content-Type
    }

}
