package pt.ua.deti.tqs.shopbackend.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ua.deti.tqs.shopbackend.model.Book;
import pt.ua.deti.tqs.shopbackend.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    Optional<Book> findById(UUID id);
    List<Book> findAll();
    List<Book> findAllByCategories(Optional<Category> category);
}
