package com.capitole.prices.api;

import com.capitole.prices.PricesMessageApplication;
import com.capitole.prices.api.response.JsonOutputPrices;
import com.capitole.prices.services.test.impl.JsonOutputPricesMother;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient(timeout = "45000")
@SpringBootTest(classes = PricesMessageApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PricesIntegrationWebTestClient {

    @Autowired
    private WebTestClient webClient;

    private static final String PARTIAL_PATH="/prices/api/v1/findPrice/";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

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
    void getPriceInControllerWithoutErrors(Long brandId, Long productId, String dateString, String startDate, String endDate, BigDecimal price) throws Exception {
        JsonOutputPricesMother jsonOutputPricesMother = new JsonOutputPricesMother();
        String urlCapitole=PARTIAL_PATH+productId+"/"+brandId+"?dateFound="+ dateString;
        JsonOutputPrices jsonOutputPrices = jsonOutputPricesMother.getJsonOutputPrices(productId,brandId,dateString, startDate, endDate, price);
        JsonOutputPrices result = this.webClient.get().uri(urlCapitole).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).exchange().expectStatus().is2xxSuccessful().expectBody(JsonOutputPrices.class).returnResult().getResponseBody();
        assertNotNull(result);
        assertEquals(jsonOutputPrices.getPrice(), result.getPrice());
        assertEquals(jsonOutputPrices.getResponse().getStrCode(), result.getResponse().getStrCode());
    }
}
