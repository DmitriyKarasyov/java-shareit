package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader(name = HEADER) Integer userId,
                                              @RequestBody @Valid ItemRequestCreationDto itemRequestCreationDto) {
        log.info("post request {} by user with id={}", itemRequestCreationDto, userId);
        return requestClient.postRequest(userId, itemRequestCreationDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequests(@RequestHeader(name = HEADER) Integer requestorId) {
        log.info("get all user requests, userId={}", requestorId);
        return requestClient.getAllUserRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(name = HEADER) Integer userId,
                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("get all requests by user with id={}, from={}, size={}", userId, from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(name = HEADER) Integer userId,
                                      @PathVariable Integer requestId) {
        log.info("get request with id={}, by user with id={}", requestId, userId);
        return requestClient.getRequestById(requestId, userId);
    }
}
