package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerIdOrderById(Integer userId, Pageable pageable);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))" +
            " and i.available = true")
    List<Item> search(String text, Pageable pageable);

    List<Item> findByRequest_Requestor_Id(Integer requestId);

    List<Item> findByRequestNotNull();

    List<Item> findByRequest_Id(Integer requestId);
}
