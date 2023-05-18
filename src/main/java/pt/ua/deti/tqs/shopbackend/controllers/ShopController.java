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
        List<Category> categories = shopService.getCategories(limit);
        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("No categories found"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(categories);
    }

    @GetMapping("/books")
    public ResponseEntity<Object> getBooks(@RequestParam(value = "limit", required = false) Integer limit,
                                           @RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "sort", required = false) String sort,
                                           @RequestParam(value = "q", required = false) String q) {
        List<Book> books = shopService.getBooks(sort,q, limit,page);
        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("No books found"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(books);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable UUID id) {
        Book book = shopService.getBookById(id);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("Book not found"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(book);
    }

    @GetMapping("/book/category/{categorySlug}")
    public ResponseEntity<Object> getBooksByCategory(@RequestParam(value = "limit", required = false) Integer limit,
                                                     @PathVariable String categorySlug) {
        List<Book> books = shopService.getBooksByCategory(categorySlug, limit);
        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .body(new ErrorDTO("No books found"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(books);
    }

}
