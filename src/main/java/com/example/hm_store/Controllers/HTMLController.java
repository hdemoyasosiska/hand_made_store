package com.example.hm_store.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;


@RestController
@RequiredArgsConstructor
public class HTMLController {

    private final ResourceLoader resourceLoader;


    @GetMapping("/home")
    public ModelAndView getHomePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home.html");
        return modelAndView;
    }

    @GetMapping("/regist")
    public ModelAndView getRegistPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("regist.html");
        return modelAndView;
    }

    @GetMapping("/catalog")
    public ModelAndView getCatalogPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("catalog.html");
        return modelAndView;
    }
    @GetMapping("/content")
    public ModelAndView getContentPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("content.html");
        return modelAndView;
    }
    @GetMapping("/about")
    public ModelAndView getAboutPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("about.html");
        return modelAndView;
    }

    @GetMapping("/items/add_item")
    public ModelAndView getAddItemPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add_item.html");
        return modelAndView;
    }

    @GetMapping("/basket")
    public ModelAndView getBasketPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("basket.html");
        return modelAndView;
    }

    @GetMapping("/individual_order")
    public ModelAndView getIndividualOrderPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("individual_order.html");
        return modelAndView;
    }

    @GetMapping("/static/css/{name}")
    public byte[] getCSS(@PathVariable String name) throws IOException {
        //Resource resource = new ClassPathResource("/static/css/" + name);
        Resource resource = resourceLoader.getResource("classpath:/static/css/" + name);
        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }

    @GetMapping("/static/img/{name}")
    public ResponseEntity<byte[]> getImage(@PathVariable String name) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:/static/img/" + name);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        }
    }

    @GetMapping("/static/js/{name}")
    public ResponseEntity<byte[]> getJS(@PathVariable String name) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:/static/js/" + name);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] jsBytes = inputStream.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("application/javascript"));
            return new ResponseEntity<>(jsBytes, headers, HttpStatus.OK);
        }
    }
}