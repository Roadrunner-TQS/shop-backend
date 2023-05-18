package pt.ua.deti.tqs.shopbackend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.shopbackend.data.*;
import pt.ua.deti.tqs.shopbackend.model.Book;
import pt.ua.deti.tqs.shopbackend.model.Category;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShopService {
    private final PickUpLocationRepository pickUpLocationRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final JwtTokenService jwtTokenService;
    private final PickUpServiceRepository pickUpServiceRepository;

    public ShopService(CategoryRepository categoryRepository, BookRepository bookRepository,
                       OrderRepository orderRepository, JwtTokenService jwtTokenService,
                       ClientRepository clientRepository, PaymentRepository paymentRepository,
                       OrderStatusRepository orderStatusRepository, PickUpServiceRepository pickUpServiceRepository,
                       PickUpLocationRepository pickUpLocationRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.jwtTokenService = jwtTokenService;
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.paymentRepository = paymentRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.pickUpServiceRepository = pickUpServiceRepository;
        this.pickUpLocationRepository = pickUpLocationRepository;
    }

    public List<Category> getCategories(Integer limit) {
        if (limit != null) {
            return categoryRepository.findAll().subList(0, limit);
        }
        return categoryRepository.findAll();
    }

    public List<Book> getBooks(String sort, String q, Integer limit, Integer page) {
        return null;
    }
}