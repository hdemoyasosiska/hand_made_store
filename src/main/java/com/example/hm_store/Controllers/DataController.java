package com.example.hm_store.Controllers;

import com.example.hm_store.Services.CustomerService;
import com.example.hm_store.Services.IndividualOrderService;
import com.example.hm_store.Services.ItemService;
import com.example.hm_store.Services.OrderService;
import com.example.hm_store.entity.Customer;
import com.example.hm_store.entity.IndividualOrder;
import com.example.hm_store.entity.Item;
import com.example.hm_store.entity.OrderFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DataController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final IndividualOrderService individualOrderService;

    @PostMapping(value = "/basket")
    public ResponseEntity<Void> sendData(@ModelAttribute Customer customer){
        customerService.save(customer);
        orderService.createOrder(customer, customer.getCart());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/individual_order", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> handleOrder(
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("email") String email,
            @RequestParam("requirements") String requirements,
            @RequestParam("budget") int budget,
            @RequestParam("color") String color,
            @RequestParam("materials") String materials,
            @RequestParam("deadline") @DateTimeFormat(pattern = "yyyy-MM-dd") Date deadline,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) throws IOException {

        // Создаем объект Customer
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);

        // Сохраняем Customer в базе данных (если нужно)
        customerService.save(customer);

        // Создаем объект IndividualOrder
        IndividualOrder order = new IndividualOrder();
        order.setCustomer(customer);
        order.setRequirements(requirements);
        order.setBudget(budget);
        order.setColor(color);
        order.setMaterials(materials);
        order.setDeadline(deadline);

        // Обработка загруженных файлов
        if (files != null && !files.isEmpty()) {
            String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
            List<OrderFile> orderFiles = new ArrayList<>();

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // Уникальное имя для каждого файла
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir, fileName);

                    // Создание директории, если она не существует
                    Files.createDirectories(filePath.getParent());

                    // Сохранение файла
                    file.transferTo(filePath.toFile());

                    // Создаем объект OrderFile и добавляем его к заказу
                    OrderFile orderFile = new OrderFile();
                    orderFile.setFileName(fileName);
                    orderFile.setIndividualOrder(order);
                    orderFiles.add(orderFile);
                }
            }

            // Привязываем файлы к заказу
            order.setFiles(orderFiles);
        }

        // Сохраняем заказ в базе данных
        individualOrderService.saveOrder(order);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/getData")
    public ResponseEntity<Page<Item>> getData(@RequestParam("page_number") int page_number,
                                              @RequestParam("countPerPage") int countPerPage){
        Page<Item> itemList = itemService.findProductsByPage(page_number, countPerPage);
        return new ResponseEntity<>(itemList, HttpStatus.OK);
    }

    @GetMapping("/getLength")
    public ResponseEntity<Long> getLength(){
        Long length = itemService.getTotalProductCount();
        return new ResponseEntity<>(length, HttpStatus.OK);
    }
}
