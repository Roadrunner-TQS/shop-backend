package pt.ua.deti.tqs.shopbackend.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.shopbackend.data.*;
import pt.ua.deti.tqs.shopbackend.model.*;
import pt.ua.deti.tqs.shopbackend.model.dto.OrderDTO;
import pt.ua.deti.tqs.shopbackend.model.enums.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
        List<Book> result = new ArrayList<>();
        List<Book> books = bookRepository.findAll();
        if (q != null && !q.equals("")) {
            for (Book book : books) {
                if (book.getTitle().toLowerCase().contains(q.toLowerCase())) {
                    result.add(book);
                }
            }
        } else {
            result.addAll(books);
        }

        if (sort != null && sort.equals("newest")) {
            result.sort((Book b1, Book b2) -> b2.getYear().compareTo(b1.getYear()));
        } else if (sort != null && sort.equals("oldest")) {
            result.sort((Book b1, Book b2) -> b1.getYear().compareTo(b2.getYear()));
        }

        if (page != null && limit == null) {
            return null;
        }

        if (page != null && limit != null) {
            int start = (page-1) * limit;
            int end = start + limit;
            if (end > result.size()) {
                end = result.size();
            }
            return result.subList(start, end);
        }

        if (limit != null) {
            return result.subList(0, limit);
        }
        return result;
    }

    public Book getBookById(UUID id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> getBooksByCategory(String categorySlug, Integer limit) {
        Category category= categoryRepository.findBySlug(categorySlug).orElse(null);
        if (category == null) {
            return null;
        }
        if (limit != null) {
            return bookRepository.findAllByCategories(Optional.of(category)).subList(0, limit);
        }
        return bookRepository.findAllByCategories(Optional.of(category));
    }
    public boolean newOrder(Order orderRequest, String token) {
        orderRequest.setDate(System.currentTimeMillis());
        String email = jwtTokenService.getEmailFromToken(token);
        if (email == null) {
            log.error("User not found");
            return false;
        }
        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client == null) {
            log.error("Client not found");
            return false;
        }
        orderRequest.setClient(client);

        List<OrderItem> items = orderRequest.getItems();
        for (OrderItem item : items) {
            UUID bookId = item.getBook().getId();
            if (bookId != null) {
                Optional<Book> optionalBook = bookRepository.findById(bookId);
                if (optionalBook.isPresent()) {
                    item.setBook(optionalBook.get());
                } else {
                    log.error("Book not found");
                    return false;
                }
            }
        }

        List<OrderStatus> orderStatuses = new ArrayList<>();
        OrderStatus orderR = new OrderStatus();
        orderR.setStatus(Status.SHIPPING);
        orderR.setTimestamp(System.currentTimeMillis());
        orderStatuses.add(orderR);
        orderStatusRepository.saveAll(orderStatuses);
        log.info("Order status saved");

        paymentRepository.save(orderRequest.getPayment());
        log.info("Payment saved");
        orderRequest.setPayment(orderRequest.getPayment());

        PickUpLocation pickUpLocation =pickUpLocationRepository.findBySlug(orderRequest.getPickUpLocation().getSlug()).orElse(null);
        if (pickUpLocation != null) {
            log.info("PickUpLocation saved");
            orderRequest.setPickUpLocation(pickUpLocation);
        }
        else{
            pickUpLocationRepository.save(orderRequest.getPickUpLocation());
            log.info("PickUpLocation saved");
            orderRequest.setPickUpLocation(orderRequest.getPickUpLocation());
        }
        PickUpService pickUpService = pickUpServiceRepository.findById(orderRequest.getPickUpLocation().getPickUpService().getId()).orElse(null);
        if (pickUpService != null) {
            log.info("PickUpService saved");
            orderRequest.setPickUpService(pickUpService);
        }
        else{
            pickUpServiceRepository.save(orderRequest.getPickUpService());
            log.info("PickUpService saved");
            orderRequest.setPickUpService(orderRequest.getPickUpService());
        }
        orderRepository.save(orderRequest);
        orderRequest.setOrderStatuses(orderStatuses);
        orderRepository.save(orderRequest);
        log.info("Order saved");
        return true;
    }
    public List<OrderDTO> getOrdersByClient(String token) {
        String email = jwtTokenService.getEmailFromToken(token);
        if (email == null) {
            log.info("User not found");
            return null;
        }
        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client == null) {
            log.info("Client not found");
            return null;
        }
        List<Order> orders = orderRepository.findAllByClient(client);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for(Order order : orders) {
            ModelMapper modelMapper = new ModelMapper();
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
            orderDTOS.add(orderDTO);
        }
        return orderDTOS;
    }
}