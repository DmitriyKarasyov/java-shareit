package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.common.RequestParser;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void searchTest() {
        Item item1 = Item.builder()
                .name("ball")
                .description("toy")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .name("doll")
                .description("toy")
                .available(true)
                .build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> toys = itemRepository.search("toy", RequestParser.makePageable(0, 2));
        assertEquals(2, toys.size());
        assertEquals(item1, toys.get(0));
        assertEquals(item2, toys.get(1));

        List<Item> balls = itemRepository.search("ball", RequestParser.makePageable(0, 2));
        assertEquals(1, balls.size());
        assertEquals(balls.get(0), item1);
    }
}
