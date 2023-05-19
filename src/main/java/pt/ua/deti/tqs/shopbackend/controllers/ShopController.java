package pt.ua.deti.tqs.shopbackend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.shopbackend.model.Book;
import pt.ua.deti.tqs.shopbackend.model.Category;
import pt.ua.deti.tqs.shopbackend.model.Order;
import pt.ua.deti.tqs.shopbackend.model.dto.ErrorDTO;
import pt.ua.deti.tqs.shopbackend.model.dto.OrderDTO;
import pt.ua.deti.tqs.shopbackend.services.ShopService;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@RestController
@RequestMapping("/api")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/categories")
    public ResponseEntity<Object> getCategories(@RequestParam(value = "limit", required = false) Integer limit){
        log.info("ShopController -- getCategories Request");
        List<Category> categories = shopService.getCategories(limit);
        if (categories.isEmpty()) {
            log.error("ShopController -- getCategories Request -- No categories found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("No categories found"));
        }
        log.info("ShopController -- getCategories Request -- Categories found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(categories);
    }

    @GetMapping("/books")
    public ResponseEntity<Object> getBooks(@RequestParam(value = "limit", required = false) Integer limit,
                                           @RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "sort", required = false) String sort,
                                           @RequestParam(value = "q", required = false) String q) {
        log.info("ShopController -- getBooks Request");
        List<Book> books = shopService.getBooks(sort,q, limit,page);
        if (books.isEmpty()) {
            log.error("ShopController -- getBooks Request -- No books found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("No books found"));
        }
        log.info("ShopController -- getBooks Request -- Books found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(books);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable UUID id) {
        log.info("ShopController -- getBookById Request");
        Book book = shopService.getBookById(id);
        if (book == null) {
            log.error("ShopController -- getBookById Request -- Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("Book not found"));
        }
        log.info("ShopController -- getBookById Request -- Book found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(book);
    }

    @GetMapping("/book/category/{categorySlug}")
    public ResponseEntity<Object> getBooksByCategory(@RequestParam(value = "limit", required = false) Integer limit,
                                                     @PathVariable String categorySlug) {
        log.info("ShopController -- getBooksByCategory Request");
        List<Book> books = shopService.getBooksByCategory(categorySlug, limit);
        if (books.isEmpty()) {
            log.error("ShopController -- getBooksByCategory Request -- No books found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("No books found"));
        }
        log.info("ShopController -- getBooksByCategory Request -- Books found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(books);
    }

    @PostMapping("/order/neworder")
    @PreAuthorize("@authService.isAuthenticated(#token)")
    public ResponseEntity<Object> createOrder(@RequestBody Order orderRequest, @RequestHeader("Authorization") String token) {
        log.info("ShopController -- createOrder Request");
        if (shopService.newOrder(orderRequest, token)) {
            log.info("ShopController -- createOrder Request -- Order created");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("Order created"));
        }
        log.error("ShopController -- createOrder Request -- Order not created");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(APPLICATION_JSON)
                .body(new ErrorDTO("Order not created"));
    }

    @GetMapping("/order")
    @PreAuthorize("@authService.isAuthenticated(#token)")
    public ResponseEntity<Object> listOrdersByClient(@RequestHeader("Authorization") String token){
        log.info("ShopController -- listOrdersByClient Request");
        List<OrderDTO> orders = shopService.getOrdersByClient(token);
        if (orders.isEmpty()) {
            log.error("ShopController -- listOrdersByClient Request -- Orders not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("Orders not found"));
        }
        log.info("ShopController -- listOrdersByClient Request -- Orders found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(orders);

    }

}
