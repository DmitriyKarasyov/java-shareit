package ru.practicum.shareit.common;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class RequestParser {
    public static Pageable makePageable(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }
        if (from >= 0 && size > 0) {
            return PageRequest.of(from / size, size);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
