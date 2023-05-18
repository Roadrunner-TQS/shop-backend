package pt.ua.deti.tqs.shopbackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.deti.tqs.shopbackend.data.*;
import pt.ua.deti.tqs.shopbackend.model.*;
import pt.ua.deti.tqs.shopbackend.model.dto.OrderDTO;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private PickUpServiceRepository pickUpServiceRepository;

    @Mock
    private PickUpLocationRepository pickUpLocationRepository;

    @InjectMocks
    private ShopService shopService;

    private Client client;
    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetCategories_WithLimit_ReturnsLimitedCategories() {
        int limit = 2;
        List<Category> categories = Arrays.asList(
                new Category(UUID.randomUUID(), "name1", "slug1"),
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = shopService.getCategories(limit);

        assertThat(result, hasSize(limit));
        assertThat(result, contains(categories.subList(0, limit).toArray()));
        verify(categoryRepository, times(1)).findAll();
    }
    @Test
    void testGetCategories_WithoutLimit_ReturnsAllCategories() {
        List<Category> categories = Arrays.asList(
                new Category(UUID.randomUUID(), "name1", "slug1"),
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = shopService.getCategories(null);

        assertThat(result, hasSize(categories.size()));
        assertThat(result, contains(categories.toArray()));
        verify(categoryRepository, times(1)).findAll();
    }
    @Test
    void testGetBooks_WithSortNewestAndQuery_ReturnsFilteredAndSortedBooks() {
        String sort = "newest";
        String q = "A Game of Thrones";
        Author author = new Author(UUID.randomUUID(),"George R. R.", "Martin", "George R. R. Martin is an American novelist and short story writer in the fantasy, horror, and science fiction genres, screenwriter, and television producer. He is best known for his series of epic fantasy novels, A Song of Ice and Fire, which was later adapted into the HBO series Game of Thrones.");
        List<Category> categories = Arrays.asList(
                new Category(UUID.randomUUID(), "name1", "slug1"),
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        List<Book> allBooks = Arrays.asList(
                new Book(UUID.randomUUID(), "A Game of Thrones", author, 10F, 0, "Leya", 1996, 694, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Clash of Kings", author, 10F, 0, "Leya", 1998, 768, "description", "image", categories.subList(0, 1))
        );

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = shopService.getBooks(sort, q, null, null);

        assertThat(result, hasSize(1));
        assertThat(result, contains(
                allBooks.get(0)
        ));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBooks_WithSortOldest_ReturnsFilteredAndSortedBooks() {
        String sort = "oldest";
        Author author = new Author(UUID.randomUUID(),"George R. R.", "Martin", "George R. R. Martin is an American novelist and short story writer in the fantasy, horror, and science fiction genres, screenwriter, and television producer. He is best known for his series of epic fantasy novels, A Song of Ice and Fire, which was later adapted into the HBO series Game of Thrones.");
        List<Category> categories = Arrays.asList(
                new Category(UUID.randomUUID(), "name1", "slug1"),
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        List<Book> allBooks = Arrays.asList(
                new Book(UUID.randomUUID(), "A Game of Thrones", author, 10F, 0, "Leya", 1996, 694, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Clash of Kings", author, 10F, 0, "Leya", 1998, 768, "description", "image", categories.subList(0, 1))
        );

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = shopService.getBooks(sort,  null, null, null);

        assertThat(result, hasSize(2));
        assertThat(result, contains(
                allBooks.get(0),
                allBooks.get(1)
        ));
        verify(bookRepository, times(1)).findAll();
    }


    @Test
    void testGetBooks_WithPage_RetunsNull(){
        Integer page = 2;
        Author author = new Author(UUID.randomUUID(),"George R. R.", "Martin", "George R. R. Martin is an American novelist and short story writer in the fantasy, horror, and science fiction genres, screenwriter, and television producer. He is best known for his series of epic fantasy novels, A Song of Ice and Fire, which was later adapted into the HBO series Game of Thrones.");
        List<Category> categories = Arrays.asList(
                new Category(UUID.randomUUID(), "name1", "slug1"),
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        List<Book> allBooks = Arrays.asList(
                new Book(UUID.randomUUID(), "A Game of Thrones", author, 10F, 0, "Leya", 1996, 694, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Clash of Kings", author, 10F, 0, "Leya", 1998, 768, "description", "image", categories.subList(0, 2)),
                new Book(UUID.randomUUID(), "A Storm of Swords", author, 10F, 0, "Leya", 2000, 992, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Feast for Crows", author, 10F, 0, "Leya", 2005, 784, "description", "image", categories.subList(0, 1))
        );

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = shopService.getBooks(null, null, null, page);

        assertThat(result, is(nullValue()));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBooks_WithLimit_RetunsLimitBooks(){
        Author author = new Author(UUID.randomUUID(),"George R. R.", "Martin", "George R. R. Martin is an American novelist and short story writer in the fantasy, horror, and science fiction genres, screenwriter, and television producer. He is best known for his series of epic fantasy novels, A Song of Ice and Fire, which was later adapted into the HBO series Game of Thrones.");
        List<Category> categories = Arrays.asList(
                new Category(UUID.randomUUID(), "name1", "slug1"),
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        List<Book> allBooks = Arrays.asList(
                new Book(UUID.randomUUID(), "A Game of Thrones", author, 10F, 0, "Leya", 1996, 694, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Clash of Kings", author, 10F, 0, "Leya", 1998, 768, "description", "image", categories.subList(0, 2)),
                new Book(UUID.randomUUID(), "A Storm of Swords", author, 10F, 0, "Leya", 2000, 992, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Feast for Crows", author, 10F, 0, "Leya", 2005, 784, "description", "image", categories.subList(0, 1))
        );

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = shopService.getBooks(null, null, 1,null);

        assertThat(result, hasSize(1));
        assertThat(result, contains(
                allBooks.get(0)
        ));
    }

    @Test
    void testGetBooks_ReturnsAllBooks() {
        Author author = new Author(UUID.randomUUID(),"George R. R.", "Martin", "George R. R. Martin is an American novelist and short story writer in the fantasy, horror, and science fiction genres, screenwriter, and television producer. He is best known for his series of epic fantasy novels, A Song of Ice and Fire, which was later adapted into the HBO series Game of Thrones.");
        List<Category> categories = Arrays.asList(
                new Category(UUID.randomUUID(), "name1", "slug1"),
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        List<Book> allBooks = Arrays.asList(
                new Book(UUID.randomUUID(), "A Game of Thrones", author, 10F, 0, "Leya", 1996, 694, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Clash of Kings", author, 10F, 0, "Leya", 1998, 768, "description", "image", categories.subList(0, 2)),
                new Book(UUID.randomUUID(), "A Storm of Swords", author, 10F, 0, "Leya", 2000, 992, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Feast for Crows", author, 10F, 0, "Leya", 2005, 784, "description", "image", categories.subList(0, 1))
        );

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = shopService.getBooks(null, null, null, null);

        assertThat(result, hasSize(4));
        assertThat(result, contains(allBooks.toArray()));
        verify(bookRepository, times(1)).findAll();
    }
    @Test
    void testGetBooks_WithPageAndLimit_ReturnsPagedBooks() {
        Integer page = 2;
        Integer limit = 3;
        Author author = new Author(UUID.randomUUID(),"George R. R.", "Martin", "George R. R. Martin is an American novelist and short story writer in the fantasy, horror, and science fiction genres, screenwriter, and television producer. He is best known for his series of epic fantasy novels, A Song of Ice and Fire, which was later adapted into the HBO series Game of Thrones.");
        List<Category> categories = Arrays.asList(
                new Category(UUID.randomUUID(), "name1", "slug1"),
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        List<Book> allBooks = Arrays.asList(
                new Book(UUID.randomUUID(), "A Game of Thrones", author, 10F, 0, "Leya", 1996, 694, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Clash of Kings", author, 10F, 0, "Leya", 1998, 768, "description", "image", categories.subList(0, 2)),
                new Book(UUID.randomUUID(), "A Storm of Swords", author, 10F, 0, "Leya", 2000, 992, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Feast for Crows", author, 10F, 0, "Leya", 2005, 784, "description", "image", categories.subList(0, 1))
        );

        when(bookRepository.findAll()).thenReturn(allBooks);

        List<Book> result = shopService.getBooks(null, null, limit, page);

        assertThat(result, hasSize(1));
        assertThat(result, contains(
                allBooks.get(3)
        ));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById_ExistingId_ReturnsBook() {
        UUID bookId = UUID.randomUUID();
        Book expectedBook = new Book(bookId, "A Game of Thrones", null, 10F, 0, "Leya", 1996, 694, "description", "image", null);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(expectedBook));

        Book result = shopService.getBookById(bookId);

        assertEquals(expectedBook, result);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testGetBookById_NonExistingId_ReturnsNull() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        Book result = shopService.getBookById(bookId);
        assertEquals(null, result);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testGetBooksByCategory_WithLimit_ReturnsLimitedBooks() {
        Integer limit = 2;
        Category category = new Category(UUID.randomUUID(), "name1", "slug1");
        Author author = new Author(UUID.randomUUID(),"George R. R.", "Martin", "George R. R. Martin is an American novelist and short story writer in the fantasy, horror, and science fiction genres, screenwriter, and television producer. He is best known for his series of epic fantasy novels, A Song of Ice and Fire, which was later adapted into the HBO series Game of Thrones.");
        List<Category> categories = Arrays.asList(
                category,
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        List<Book> allBooks = Arrays.asList(
                new Book(UUID.randomUUID(), "A Game of Thrones", author, 10F, 0, "Leya", 1996, 694, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Clash of Kings", author, 10F, 0, "Leya", 1998, 768, "description", "image", categories.subList(0, 2)),
                new Book(UUID.randomUUID(), "A Storm of Swords", author, 10F, 0, "Leya", 2000, 992, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Feast for Crows", author, 10F, 0, "Leya", 2005, 784, "description", "image", categories.subList(0, 1))
        );

        when(categoryRepository.findBySlug(category.getSlug())).thenReturn(Optional.of(category));
        when(bookRepository.findAllByCategories(Optional.of(category))).thenReturn(allBooks);

        List<Book> result = shopService.getBooksByCategory(category.getSlug(), limit);

        assertThat(result, hasSize(2));
        assertThat(result, contains(
                allBooks.get(0),
                allBooks.get(1)
        ));
        verify(categoryRepository, times(1)).findBySlug(category.getSlug());
        verify(bookRepository, times(1)).findAllByCategories(Optional.of(category));
    }

    @Test
    void testGetBooksByCategory_NoLimit_ReturnsAllBooks() {
        Category category = new Category(UUID.randomUUID(), "name1", "slug1");
        Author author = new Author(UUID.randomUUID(),"George R. R.", "Martin", "George R. R. Martin is an American novelist and short story writer in the fantasy, horror, and science fiction genres, screenwriter, and television producer. He is best known for his series of epic fantasy novels, A Song of Ice and Fire, which was later adapted into the HBO series Game of Thrones.");
        List<Category> categories = Arrays.asList(
                category,
                new Category(UUID.randomUUID(), "name2", "slug2"),
                new Category(UUID.randomUUID(), "name3", "slug3")
        );

        List<Book> allBooks = Arrays.asList(
                new Book(UUID.randomUUID(), "A Game of Thrones", author, 10F, 0, "Leya", 1996, 694, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Clash of Kings", author, 10F, 0, "Leya", 1998, 768, "description", "image", categories.subList(0, 2)),
                new Book(UUID.randomUUID(), "A Storm of Swords", author, 10F, 0, "Leya", 2000, 992, "description", "image", categories.subList(0, 1)),
                new Book(UUID.randomUUID(), "A Feast for Crows", author, 10F, 0, "Leya", 2005, 784, "description", "image", categories.subList(0, 1))
        );

        when(categoryRepository.findBySlug(category.getSlug())).thenReturn(Optional.of(category));
        when(bookRepository.findAllByCategories(Optional.of(category))).thenReturn(allBooks);

        List<Book> result = shopService.getBooksByCategory(category.getSlug(), null);

        assertThat(result, hasSize(4));
        assertThat(result, contains(allBooks.toArray()));
        verify(categoryRepository, times(1)).findBySlug(category.getSlug());
        verify(bookRepository, times(1)).findAllByCategories(Optional.of(category));
    }

    @Test
    void testGetBooksByCategory_WithCategoryNotFound_ReturnsNull() {
        String slug = "slug";

        when(categoryRepository.findBySlug(slug)).thenReturn(Optional.empty());

        List<Book> result = shopService.getBooksByCategory(slug, null);

        assertNull(result);
        verify(categoryRepository, times(1)).findBySlug(slug);
        verify(bookRepository, never()).findAllByCategories(any());
    }
    @Test
    void testGetOrdersByClient_ValidToken_ReturnsOrderDTOList() {
        String token = "valid_token";
        String email = "test@example.com";
        Client client = new Client();
        client.setEmail(email);
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());

        when(jwtTokenService.getEmailFromToken(token)).thenReturn(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
        when(orderRepository.findAllByClient(client)).thenReturn(orders);

        List<OrderDTO> result = shopService.getOrdersByClient(token);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jwtTokenService, times(1)).getEmailFromToken(token);
        verify(clientRepository, times(1)).findByEmail(email);
        verify(orderRepository, times(1)).findAllByClient(client);
    }

    @Test
    void testGetOrdersByClient_InvalidToken_ReturnsNull() {
        String token = "invalid_token";

        when(jwtTokenService.getEmailFromToken(token)).thenReturn(null);

        List<OrderDTO> result = shopService.getOrdersByClient(token);

        assertNull(result);
        verify(jwtTokenService, times(1)).getEmailFromToken(token);
        verify(clientRepository, never()).findByEmail(anyString());
        verify(orderRepository, never()).findAllByClient(any());
    }

    @Test
    void testGetOrdersByClient_ClientNotFound_ReturnsNull() {
        String token = "valid_token";
        String email = "test@example.com";

        when(jwtTokenService.getEmailFromToken(token)).thenReturn(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());

        List<OrderDTO> result = shopService.getOrdersByClient(token);

        assertNull(result);
        verify(jwtTokenService, times(1)).getEmailFromToken(token);
        verify(clientRepository, times(1)).findByEmail(email);
        verify(orderRepository, never()).findAllByClient(any());
    }

    @Test
    void testNewOrder_ValidOrder_SuccessfullySaved() {
        String token = "valid_token";
        String email = "test@example.com";
        UUID bookId = UUID.randomUUID();
        Order orderRequest = new Order();
        orderRequest.setPayment(new Payment());
        orderRequest.setPickUpLocation(new PickUpLocation());
        orderRequest.getPickUpLocation().setPickUpService(new PickUpService());
        OrderItem orderItem = new OrderItem();
        Book book = new Book();
        book.setId(bookId);
        orderItem.setBook(book);
        orderRequest.setItems(Collections.singletonList(orderItem));

        when(jwtTokenService.getEmailFromToken(token)).thenReturn(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(new Client()));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(new Book()));
        when(pickUpLocationRepository.findBySlug(orderRequest.getPickUpLocation().getSlug())).thenReturn(Optional.empty());
        when(pickUpServiceRepository.findById(orderRequest.getPickUpLocation().getPickUpService().getId())).thenReturn(Optional.empty());
        when(orderRepository.save(orderRequest)).thenReturn(orderRequest);

        boolean result = shopService.newOrder(orderRequest, token);

        assertTrue(result);
        verify(jwtTokenService, times(1)).getEmailFromToken(token);
        verify(clientRepository, times(1)).findByEmail(email);
        verify(bookRepository, times(1)).findById(bookId);
        verify(orderRepository, times(2)).save(orderRequest);
    }

    @Test
    void testNewOrder_InvalidToken_ReturnsFalse() {
        String token = "invalid_token";
        Order orderRequest = new Order();

        when(jwtTokenService.getEmailFromToken(token)).thenReturn(null);

        boolean result = shopService.newOrder(orderRequest, token);

        assertFalse(result);
        verify(jwtTokenService, times(1)).getEmailFromToken(token);
        verify(clientRepository, never()).findByEmail(anyString());
        verify(bookRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }


}