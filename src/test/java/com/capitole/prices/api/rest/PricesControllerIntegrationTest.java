package com.capitole.prices.api.rest;

import com.capitole.prices.PricesMessageApplication;
import com.capitole.prices.domain.dto.Price;
import com.capitole.prices.output.objects.JsonOutputPrices;
import com.capitole.prices.services.test.impl.JsonOutputPricesMother;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PricesMessageApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PricesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    /*-    Test 1: petici�n a las 10:00 del d�a 14 del producto 35455   para la brand 1 (ZARA)
-          Test 2: petici�n a las 16:00 del d�a 14 del producto 35455   para la brand 1 (ZARA)
-          Test 3: petici�n a las 21:00 del d�a 14 del producto 35455   para la brand 1 (ZARA)
-          Test 4: petici�n a las 10:00 del d�a 15 del producto 35455   para la brand 1 (ZARA)
-          Test 5: petici�n a las 21:00 del d�a 16 del producto 35455   para la brand 1 (ZARA)
    * */
    public static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(1L, 35455L, "2020-06-14-10.00.00", "2020-06-14 00:00:00.0", "2020-12-31 23:59:59.0", new BigDecimal("35.5")),
                Arguments.of(1L, 35455L, "2020-06-14-16.00.00", "2020-06-14 15:00:00.0", "2020-06-14 18:30:00.0", new BigDecimal("25.45")),
                Arguments.of(1L, 35455L, "2020-06-14-21.00.00", "2020-06-14 00:00:00.0", "2020-12-31 23:59:59.0", new BigDecimal("35.5")),
                Arguments.of(1L, 35455L, "2020-06-15-10.00.00", "2020-06-15 00:00:00.0", "2020-06-15 11:00:00.0", new BigDecimal("30.5")),
                Arguments.of(1L, 35455L, "2020-06-16-21.00.00", "2020-06-15 16:00:00.0", "2020-12-31 23:59:59.0", new BigDecimal("38.95"))
                );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void givenPriceWithoutErrors(Long brandId, Long productId, String dateString, String startDate, String endDate, BigDecimal price) throws Exception {
        JsonOutputPricesMother jsonOutputPricesMother = new JsonOutputPricesMother();
        JsonOutputPrices jsonOutputPrices = jsonOutputPricesMother.getJsonOutputPrices(productId,brandId,dateString, startDate, endDate, price);
        this.mockMvc
                .perform(
                        get("/prices/api/v1/findPrice/"+productId+"/"+brandId+"?dateFound="+ dateString)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(jsonOutputPrices)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.rateToApply").value(price));
    }


}
