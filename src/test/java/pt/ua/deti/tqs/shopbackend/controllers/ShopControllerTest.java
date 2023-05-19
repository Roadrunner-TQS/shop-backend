package pt.ua.deti.tqs.shopbackend.controllers;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import pt.ua.deti.tqs.shopbackend.model.Author;
import pt.ua.deti.tqs.shopbackend.model.Book;
import pt.ua.deti.tqs.shopbackend.model.Category;
import pt.ua.deti.tqs.shopbackend.model.Order;
import pt.ua.deti.tqs.shopbackend.model.dto.OrderDTO;
import pt.ua.deti.tqs.shopbackend.services.ShopService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@WebMvcTest(ShopController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopService shopService;

    private List<Category> categories;
    private List<Book> books;
    private Book book;

    @BeforeEach()
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        categories = new ArrayList<>();
        categories.add(new Category(UUID.randomUUID(), "category1", "category1"));
        categories.add(new Category(UUID.randomUUID(), "category2", "category2"));
        categories.add(new Category(UUID.randomUUID(), "category3", "category3"));


        Author author = new Author(UUID.randomUUID(),"George R. R.", "Martin", "George R. R. Martin is an American novelist and short story writer in the fantasy, horror, and science fiction genres, screenwriter, and television producer. He is best known for his series of epic fantasy novels, A Song of Ice and Fire, which was later adapted into the HBO series Game of Thrones.");
        book = new Book(UUID.randomUUID(), "A Game of Thrones", author, 10F, 0, "Leya", 1996, 694, "description", "image", categories.subList(0, 1));
        books = new ArrayList<>();
        books.add(book);
        books.add(new Book(UUID.randomUUID(), "A Clash of Kings", author, 10F, 0, "Leya", 1998, 768, "description", "image", categories.subList(0, 1)));

    }

    @Test
    void whenGetCategories_thenReturnOk() {
        when(shopService.getCategories(null)).thenReturn(categories);
        RestAssuredMockMvc.given()
                .when()
                .get("/api/categories")
                .then()
                .statusCode(200)
                .body("size()", equalTo(3))
                .body("[0].id", equalTo(categories.get(0).getId().toString()))
                .body("[0].name", equalTo(categories.get(0).getName()))
                .body("[0].slug", equalTo(categories.get(0).getSlug()))
                .body("[1].id", equalTo(categories.get(1).getId().toString()))
                .body("[1].name", equalTo(categories.get(1).getName()))
                .body("[1].slug", equalTo(categories.get(1).getSlug()))
                .body("[2].id", equalTo(categories.get(2).getId().toString()))
                .body("[2].name", equalTo(categories.get(2).getName()))
                .body("[2].slug", equalTo(categories.get(2).getSlug()));

        verify(shopService).getCategories(null);
    }

    @Test
    void whenGetCategoriesWithCategoriesIsEmpty_thenReturnNotFound() {
        when(shopService.getCategories(null)).thenReturn(new ArrayList<>());
        RestAssuredMockMvc.given()
                .when()
                .get("/api/categories")
                .then()
                .statusCode(404)
                .body("message", equalTo("No categories found"));

        verify(shopService).getCategories(null);
    }

    @Test
    void whenGetCategoriesWithfilters_thenReturnOk() {
        when(shopService.getCategories(any())).thenReturn(categories.subList(0, 2));
        RestAssuredMockMvc.given()
                .when()
                .get("/api/categories?limit=2")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].id", equalTo(categories.get(0).getId().toString()))
                .body("[0].name", equalTo(categories.get(0).getName()))
                .body("[0].slug", equalTo(categories.get(0).getSlug()))
                .body("[1].id", equalTo(categories.get(1).getId().toString()))
                .body("[1].name", equalTo(categories.get(1).getName()))
                .body("[1].slug", equalTo(categories.get(1).getSlug()));

        verify(shopService).getCategories(2);
    }

    @Test
    void whenGetCategoriesWithfilters_thenReturnNotFound(){
        when(shopService.getCategories(any())).thenReturn(new ArrayList<>());
        RestAssuredMockMvc.given()
                .when()
                .get("/api/categories?limit=2")
                .then()
                .statusCode(404)
                .body("message", equalTo("No categories found"));

        verify(shopService).getCategories(any());
    }


    @Test
    void whenGetBooks_thenReturnOK(){
        when(shopService.getBooks(null, null, null, null)).thenReturn(books);
        RestAssuredMockMvc.given()
                .when()
                .get("/api/books")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));

        verify(shopService, times(1)).getBooks(null, null, null, null);

    }
    @Test
    void whenGetBooksWithBookIsEmpty_thenReturnNotFound(){
        when(shopService.getBooks(null, null, null, null)).thenReturn(new ArrayList<>());
        RestAssuredMockMvc.given()
                .when()
                .get("/api/books")
                .then()
                .statusCode(404)
                .body("message", equalTo("No books found"));

        verify(shopService, times(1)).getBooks(null, null, null, null);

    }

    @Test
    void whenGetBooksWithFIlters_thenReturnNotFound(){
        when(shopService.getBooks(any(), any(), any(), any())).thenReturn(new ArrayList<>());
        RestAssuredMockMvc.given()
                .when()
                .get("/api/books?limit=1")
                .then()
                .statusCode(404)
                .body("message", equalTo("No books found"));

        verify(shopService, times(1)).getBooks(any(), any(), any(), any());
    }

    @Test
    void whenGetBooksWithFilter_thenReturnOk(){
        when(shopService.getBooks(any(), any(), any(), any())).thenReturn(books.subList(0, 1));
        RestAssuredMockMvc.given()
                .when()
                .get("/api/books?limit=1&q=A&sort=newest&page=1")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));

        verify(shopService, times(1)).getBooks(any(), any(), any(), any());
    }

    @Test
    void whenGetBookById_thenReturnsOKAndBook(){
        when(shopService.getBookById(any())).thenReturn(book);


        RestAssuredMockMvc.given()
                .when()
                .get("/api/book/"+ UUID.randomUUID().toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(book.getId().toString()))
                .body("title", equalTo(book.getTitle()))
                .body("author.id", equalTo(book.getAuthor().getId().toString()))
                .body("author.firstName", equalTo(book.getAuthor().getFirstName()))
                .body("author.lastName", equalTo(book.getAuthor().getLastName()))
                .body("author.bio", equalTo(book.getAuthor().getBio()))
                .body("price", equalTo(book.getPrice()))
                .body("discount", equalTo(book.getDiscount()))
                .body("publisher", equalTo(book.getPublisher()))
                .body("year", equalTo(book.getYear()))
                .body("pages", equalTo(book.getPages()))
                .body("description", equalTo(book.getDescription()))
                .body("imageUrl", equalTo(book.getImageUrl()))
                .body("categories.size()", equalTo(1))
                .body("categories[0].id", equalTo(book.getCategories().iterator().next().getId().toString()))
                .body("categories[0].name", equalTo(book.getCategories().iterator().next().getName()))
                .body("categories[0].slug", equalTo(book.getCategories().iterator().next().getSlug()));

        verify(shopService, times(1)).getBookById(any());
    }

    @Test
    void whenGetBookById_thenReturnsNotFound(){
        when(shopService.getBookById(any())).thenReturn(null);

        RestAssuredMockMvc.given()
                .when()
                .get("/api/book/"+ UUID.randomUUID().toString())
                .then()
                .statusCode(404)
                .body("message", equalTo("Book not found"));

        verify(shopService, times(1)).getBookById(any());
    }


    @Test
    void whenGetBookByCategory_thenReturnsOKAndBook() {
        when(shopService.getBooksByCategory(any(), any())).thenReturn(books.subList(0, 1));

        RestAssuredMockMvc.given()
                .when()
                .get("/api/book/category/ola")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));

        verify(shopService, times(1)).getBooksByCategory(any(), any());
    }

    @Test
    void whenGetBookByCategory_thenReturnsNotFound() {
        when(shopService.getBooksByCategory(any(), any())).thenReturn(new ArrayList<>());

        RestAssuredMockMvc.given()
                .when()
                .get("/api/book/category/ola")
                .then()
                .statusCode(404)
                .body("message", equalTo("No books found"));

        verify(shopService, times(1)).getBooksByCategory(any(), any());
    }

    @Test
    @WithMockUser
    void whenCreateOrder_thenReturnsCreated() {
        when(shopService.newOrder(any(), any())).thenReturn(true);

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .header("Authorization", "Bearer token")
                .body(new Order())
                .when()
                .post("/api/order/neworder")
                .then()
                .statusCode(201);
    }

    @Test
    @WithMockUser
    void whenCreateOrder_thenReturnsBadRequest() {
        when(shopService.newOrder(any(), any())).thenReturn(false);

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .header("Authorization", "Bearer token")
                .body(new Order())
                .when()
                .post("/api/order/neworder")
                .then()
                .statusCode(400);
    }

    @Test
    @WithMockUser
    void whenListOrder_thenReturnsOK() {
        OrderDTO order = new OrderDTO();
        ArrayList<OrderDTO> list = new ArrayList<>();
        list.add(order);
        when(shopService.getOrdersByClient(any())).thenReturn(list);

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .header("Authorization", "Bearer token")
                .when()
                .get("/api/order")
                .then()
                .statusCode(200);
    }

    @Test
    @WithMockUser
    void whenListOrder_thenReturnsNotFound() {
        ArrayList<OrderDTO> list = new ArrayList<>();

        when(shopService.getOrdersByClient(any())).thenReturn(list);

        RestAssuredMockMvc.given()
                .contentType("application/json")
                .header("Authorization", "Bearer token")
                .when()
                .get("/api/order")
                .then()
                .statusCode(404);
    }
}
