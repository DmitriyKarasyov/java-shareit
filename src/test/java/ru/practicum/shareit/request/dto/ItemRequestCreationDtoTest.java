package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.*;

@JsonTest
public class ItemRequestCreationDtoTest {
    @Autowired
    private JacksonTester<ItemRequestCreationDto> json;

    @Test
    public void itemRequestCreationDtoTest() throws Exception {
        ItemRequestCreationDto itemRequestCreationDto = ItemRequestCreationDto.builder()
                .description("requestDescription")
                .userId(1)
                .build();

        JsonContent<ItemRequestCreationDto> result = json.write(itemRequestCreationDto);

        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("requestDescription");
    }
}
