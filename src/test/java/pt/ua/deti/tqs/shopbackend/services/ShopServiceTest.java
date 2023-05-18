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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
    public void testGetCategories_WithoutLimit_ReturnsAllCategories() {
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
}