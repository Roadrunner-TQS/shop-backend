package pt.ua.deti.tqs.shopbackend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pt.ua.deti.tqs.shopbackend.data.*;
import pt.ua.deti.tqs.shopbackend.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final PickUpServiceRepository pickUpServiceRepository;
    private final ClientRepository clientRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public DataInitializer(CategoryRepository categoryRepository, BookRepository bookRepository, AuthorRepository authorRepository,
                           ClientRepository clientRepository,
                           PickUpServiceRepository pickUpServiceRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.clientRepository = clientRepository;
        this.pickUpServiceRepository = pickUpServiceRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("DataInitializer started");
        if (categoryRepository.count() == 0) {
            log.info("No categories found, creating some...");
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("shop-backend-login/src/main/resources/data/categories.json");
            List<Category> categories = mapper.readValue(file, new TypeReference<ArrayList<Category>>() {});
            categoryRepository.saveAll(categories);
            log.info("Categories created");
        }
        if (authorRepository.count() == 0) {
            log.info("No authors found, creating some...");
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("shop-backend-login/src/main/resources/data/authors.json");
            List<Author> authors = mapper.readValue(file, new TypeReference<ArrayList<Author>>() {});
            authorRepository.saveAll(authors);
            log.info("Authors created");
        }

        if (bookRepository.count() == 0) {
            log.info("No books found, creating some...");
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("shop-backend-login/src/main/resources/data/books.json");
            List<Book> books = mapper.readValue(file, new TypeReference<ArrayList<Book>>() {});

            bookRepository.saveAll(books);
            log.info("Books created");
        }

        if (clientRepository.count() == 0) {
            log.info("No clients found, creating some...");
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("shop-backend-login/src/main/resources/data/clients.json");
            List<Client> books = mapper.readValue(file, new TypeReference<ArrayList<Client>>() {});
            clientRepository.saveAll(books);
            log.info("Clients created");
        }

        if (pickUpServiceRepository.count() == 0) {
            log.info("No PickUpServices found, creating some...");
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("shop-backend-login/src/main/resources/data/pickUpServices.json");
            List<PickUpService> pickUpServices = mapper.readValue(file, new TypeReference<ArrayList<PickUpService>>() {});
            pickUpServiceRepository.saveAll(pickUpServices);
            log.info("PickUpServices created");
        }

        log.info("DataInitializer finished");
    }

}
