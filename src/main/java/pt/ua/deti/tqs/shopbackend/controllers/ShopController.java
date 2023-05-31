package pt.ua.deti.tqs.shopbackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.shopbackend.model.Book;
import pt.ua.deti.tqs.shopbackend.model.Category;
import pt.ua.deti.tqs.shopbackend.model.Order;
import pt.ua.deti.tqs.shopbackend.model.PickUpLocation;
import pt.ua.deti.tqs.shopbackend.model.dto.ErrorDTO;
import pt.ua.deti.tqs.shopbackend.model.dto.OrderDTO;
import pt.ua.deti.tqs.shopbackend.services.ShopService;
import pt.ua.deti.tqs.shopbackend.services.pickup.PickUpService;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;
    private final PickUpService pickUpService;

    @GetMapping("/categories")
    public ResponseEntity<Object> getCategories(@RequestParam(value = "limit", required = false) Integer limit){
        log.info("ShopController -- getCategories -- request received");
        List<Category> categories = shopService.getCategories(limit);
        log.info("ShopController -- getCategories -- Categories found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(categories);
    }

    @GetMapping("/books")
    public ResponseEntity<Object> getBooks(@RequestParam(value = "limit", required = false) Integer limit,
                                           @RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "sort", required = false) String sort,
                                           @RequestParam(value = "q", required = false) String q) {
        log.info("ShopController -- getBooks -- request received");
        List<Book> books = shopService.getBooks(sort,q, limit,page);
        log.info("ShopController -- getBooks -- Books found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(books);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable UUID id) {
        log.info("ShopController -- getBookById -- request received");
        Book book = shopService.getBookById(id);
        if (book == null) {
            log.error("ShopController -- getBookById -- Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("Book not found"));
        }
        log.info("ShopController -- getBookById -- Book found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(book);
    }

    @GetMapping("/book/category/{categorySlug}")
    public ResponseEntity<Object> getBooksByCategory(@RequestParam(value = "limit", required = false) Integer limit,
                                                     @PathVariable String categorySlug) {
        log.info("ShopController -- getBooksByCategory -- request received");
        List<Book> books = shopService.getBooksByCategory(categorySlug, limit);
        log.info("ShopController -- getBooksByCategory -- Books found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(books);
    }

    @PostMapping("/order")
    @PreAuthorize("@authService.isAuthenticated(#token)")
    public ResponseEntity<Object> createOrder(@RequestBody Order orderRequest, @RequestHeader("Authorization") String token) {
        log.info("ShopController -- createOrder -- request received");
        if (shopService.newOrder(orderRequest, token)) {
            log.info("ShopController -- createOrder -- Order created");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("Order created"));
        }
        log.error("ShopController -- createOrder -- Order not created");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(APPLICATION_JSON)
                .body(new ErrorDTO("Order not created"));
    }

    @GetMapping("/order")
    @PreAuthorize("@authService.isAuthenticated(#token)")
    public ResponseEntity<Object> listOrdersByClient(@RequestHeader("Authorization") String token){
        log.info("ShopController -- listOrdersByClient -- request received");
        List<OrderDTO> orders = shopService.getOrdersByClient(token);
        log.info("ShopController -- listOrdersByClient -- Orders found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(orders);

    }

    @GetMapping("/pickup")
    public ResponseEntity<Object> listPickupOrders() {
        log.info("ShopController -- listPickupOrders -- request received");
        List<PickUpLocation> pickUps = pickUpService.getPickUpLocations();
        if (pickUps == null) {
            log.error("ShopController -- listPickupOrders -- Pickup locations not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("Pickup locations not found"));
        }
        log.info("ShopController -- listPickupOrders -- Pickup locations found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(pickUps);
    }

    @PutMapping("/cancel")
    @PreAuthorize("@authService.isAuthenticated(#token)")
    public ResponseEntity<Object> cancelOrder(@RequestParam UUID id, @RequestHeader("Authorization") String token) {
        log.info("ShopController -- cancelOrder -- request received");
        if (pickUpService.cancelOrder(id)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("Order canceled"));
        }
        log.error("ShopController -- cancelOrder -- Order not canceled");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(APPLICATION_JSON)
                .body(new ErrorDTO("Order not canceled"));
    }
}
