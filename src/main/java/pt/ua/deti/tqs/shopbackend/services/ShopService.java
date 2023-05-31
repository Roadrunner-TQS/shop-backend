package pt.ua.deti.tqs.shopbackend.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.shopbackend.data.*;
import pt.ua.deti.tqs.shopbackend.model.*;
import pt.ua.deti.tqs.shopbackend.model.dto.OrderDTO;
import pt.ua.deti.tqs.shopbackend.model.enums.Status;
import pt.ua.deti.tqs.shopbackend.services.pickup.PackDto;
import pt.ua.deti.tqs.shopbackend.services.pickup.PickupApi;
import pt.ua.deti.tqs.shopbackend.services.pickup.RoadRunnerPackageEntity;

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
    private final PickupApi api;

    public ShopService(CategoryRepository categoryRepository, BookRepository bookRepository,
                       OrderRepository orderRepository, JwtTokenService jwtTokenService,
                       ClientRepository clientRepository, PaymentRepository paymentRepository,
                       OrderStatusRepository orderStatusRepository, PickUpServiceRepository pickUpServiceRepository,
                       PickUpLocationRepository pickUpLocationRepository,
                       PickupApi api) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.jwtTokenService = jwtTokenService;
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.paymentRepository = paymentRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.pickUpServiceRepository = pickUpServiceRepository;
        this.pickUpLocationRepository = pickUpLocationRepository;
        this.api = api;
    }

    public List<Category> getCategories(Integer limit) {
        log.info("ShopService -- getCategories -- Request");
        if (limit != null) {
            log.info("ShopService -- getCategories -- Request with limit: " + limit);
            return categoryRepository.findAll().subList(0, limit);
        }
        log.info("ShopService -- getCategories --  Request without limit");
        return categoryRepository.findAll();
    }

    public List<Book> getBooks(String sort, String q, Integer limit, Integer page) {
        log.info("ShopService -- getBooks -- Request");
        List<Book> result = new ArrayList<>();
        List<Book> books = bookRepository.findAll();
        if (q != null && !q.equals("")) {
            log.info("ShopService -- getBooks -- Request with query: " + q);
            for (Book book : books) {
                if (book.getTitle().toLowerCase().contains(q.toLowerCase())) {
                    result.add(book);
                }
            }
        } else {
            result.addAll(books);
        }

        if (sort != null && sort.equals("newest")) {
            log.info("ShopService -- getBooks -- Request with sort: " + sort);
            result.sort((Book b1, Book b2) -> b2.getYear().compareTo(b1.getYear()));
        } else if (sort != null && sort.equals("oldest")) {
            log.info("ShopService -- getBooks -- Request with sort: " + sort);
            result.sort((Book b1, Book b2) -> b1.getYear().compareTo(b2.getYear()));
        }

        if (page != null && limit == null) {
            log.error("ShopService -- getBooks -- Request Not Valid");
            return new ArrayList<>();
        }

        if (page != null) {
            int start = (page-1) * limit;
            int end = start + limit;
            if (end > result.size()) {
                end = result.size();
            }
            log.info("ShopService -- getBooks -- Request with page: " + page + " and limit: " + limit);
            return result.subList(start, end);
        }

        if (limit != null) {
            log.info("ShopService -- getBooks -- Request with limit: " + limit);
            return result.subList(0, limit);
        }
        log.info("ShopService -- getBooks -- Request without limit");
        return result;
    }

    public Book getBookById(UUID id) {
        log.info("ShopService -- getBookById  -- Request");
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> getBooksByCategory(String categorySlug, Integer limit) {
        log.info("ShopService -- getBooksByCategory  -- Request");
        Category category= categoryRepository.findBySlug(categorySlug).orElse(null);
        if (category == null) {
            log.error("ShopService -- getBooksByCategory -- No Category Found");
            return new ArrayList<>();
        }
        if (limit != null) {
            log.info("ShopService -- getBooksByCategory -- Request with limit: " + limit);
            return bookRepository.findAllByCategories(Optional.of(category)).subList(0, limit);
        }
        log.info("ShopService -- getBooksByCategory -- Request without limit");
        return bookRepository.findAllByCategories(Optional.of(category));
    }
    public List<OrderDTO> getOrdersByClient(String token) {
        log.info("ShopService -- getOrdersByClient -- Request received");
        String email = jwtTokenService.getEmailFromToken(token);
        if (email == null) {
            log.info("ShopService -- getOrdersByClient -- No Email Found");
            return new ArrayList<>();
        }
        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client == null) {
            log.info("ShopService -- getOrdersByClient -- No Client Found");
            return new ArrayList<>();
        }
        List<Order> orders = orderRepository.findAllByClient(client);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for(Order order : orders) {
            ModelMapper modelMapper = new ModelMapper();
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
            orderDTOS.add(orderDTO);
        }
        log.info("ShopService -- getOrdersByClient -- Request Success");
        return orderDTOS;
    }
    public boolean newOrder(Order orderRequest, String token) {
        log.info("ShopService -- newOrder -- Request");
        orderRequest.setDate(System.currentTimeMillis());
        String email = jwtTokenService.getEmailFromToken(token);
        if (email == null) {
            log.error("ShopService -- newOrder -- No Email Found");
            return false;
        }
        Client client = clientRepository.findByEmail(email).orElse(null);
        if (client == null) {
            log.error("ShopService -- newOrder -- No Client Found");
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
                    log.error("ShopService -- newOrder -- No Book Found");
                    return false;
                }
            }
        }

        List<OrderStatus> orderStatuses = new ArrayList<>();
        OrderStatus orderR = new OrderStatus();
        orderR.setStatus(Status.PENDING);
        orderR.setTimestamp(System.currentTimeMillis());
        orderStatuses.add(orderR);

        PickUpLocation pickUpLocation = pickUpLocationRepository.findBySlug(orderRequest.getPickUpLocation().getSlug()).orElse(null);
        if (pickUpLocation == null) {
            log.info("ShopService -- newOrder -- No PickUpLocation Found");
            return false;
        }
        orderRequest.setPickUpLocation(pickUpLocation);
        PickUpService pickUpService = pickUpServiceRepository.findBySlug(orderRequest.getPickUpLocation().getPickUpService().getSlug()).orElse(null);
        if (pickUpService == null) {
            log.info("ShopService -- newOrder -- No PickUpService Found");
            return false;
        }
        orderRequest.setPickUpService(pickUpService);

        paymentRepository.save(orderRequest.getPayment());
        orderRequest.setPayment(orderRequest.getPayment());

        orderRepository.save(orderRequest);
        orderStatusRepository.saveAll(orderStatuses);
        orderRequest.setOrderStatus(orderStatuses);
        orderRepository.save(orderRequest);
        PackDto pack = new PackDto(client.getFirstName(), client.getLastName(), client.getEmail(),
                Long.parseLong(client.getPhone()), pickUpLocation.getId(), UUID.fromString("6c84fb90-12c4-11e1-840d-7b25c5ee775d"), orderR.getTimestamp());

        RoadRunnerPackageEntity rrpe = api.createPackage(pack);
        if (rrpe == null) {
            log.error("ShopService -- newOrder -- RoadRunner Error");
            orderRepository.delete(orderRequest);
            return false;
        }
        orderRequest.setTrackingId(rrpe.getId());
        orderRepository.save(orderRequest);
        log.info("ShopService -- newOrder -- Request Success");
        return true;
    }
}