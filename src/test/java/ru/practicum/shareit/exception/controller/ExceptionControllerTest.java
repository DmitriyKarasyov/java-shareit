package ru.practicum.shareit.exception.controller;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionControllerTest {
    @Test
    public void notFoundTest() {
        ExceptionController exceptionController = new ExceptionController();
        NotFoundException notFoundException = new NotFoundException("Booking", 123);

        assertEquals("Booking with id = 123 is not found", exceptionController.notFoundHandler(notFoundException));
    }

    @Test
    public void testDuplicate() {
        DuplicateEmailException exception = new DuplicateEmailException("ivan@ivanov.ru");
        ExceptionController exceptionController = new ExceptionController();

        assertEquals("Электронная почта ivan@ivanov.ru уже добавлена.",
                exceptionController.duplicateEmailHandler(exception));
    }

    @Test
    public void testWrongUser() {
        WrongUserException exception = new WrongUserException(123);
        ExceptionController exceptionController = new ExceptionController();

        assertEquals("Данная вещь не размещалась пользователем с id = 123",
                exceptionController.wrongUserHandler(exception));
    }

    @Test
    public void unavailableItemTest() {
        UnavailableItemException exception = new UnavailableItemException(123);
        ExceptionController exceptionController = new ExceptionController();

        assertEquals("Вещь с id = 123",
                exceptionController.unavailableItemHandler(exception));
    }

    @Test
    public void wrongBookingTimeTest() {
        WrongBookingTimeException exception = new WrongBookingTimeException();
        ExceptionController exceptionController = new ExceptionController();

        assertEquals("Время бронирования задано некорректно",
                exceptionController.wrongBookingTimeHandler(exception));
    }

    @Test
    public void wrongRightsTest() {
        WrongBookingRightsException exception = new WrongBookingRightsException(123);
        ExceptionController exceptionController = new ExceptionController();

        assertEquals("Пользователь с id = 123 не может осуществлять данные действия",
                exceptionController.wrongBookingRightsHandler(exception));
    }

    @Test
    public void unkownStateTest() {
        UnknownStateException exception = new UnknownStateException("qqq");
        ExceptionController exceptionController = new ExceptionController();

        assertEquals("Unknown state: qqq",
                exceptionController.unknownStateHandler(exception).get("error"));
    }

    @Test
    public void sameStatusTest() {
        SameStatusException exception = new SameStatusException(123, BookingStatus.APPROVED);
        ExceptionController exceptionController = new ExceptionController();

        assertEquals("Booking with id = 123 already has status APPROVED",
                exceptionController.sameStatusHandler(exception).get("error"));
    }

    @Test
    public void commentTest() {
        CommentException exception = new CommentException("comment");
        ExceptionController exceptionController = new ExceptionController();

        assertEquals("comment",
                exceptionController.commentExceptionHandler(exception).get("error"));
    }
}
