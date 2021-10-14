package com.capitole.prices.api;

import com.capitole.prices.api.rest.PricesController;
import com.capitole.prices.api.response.JsonOutputPrices;
import com.capitole.prices.domain.ports.primary.AdapterPriceToJsonOutputService;
import com.capitole.prices.services.test.impl.JsonOutputPricesMother;
import com.capitole.prices.utils.LocalDateFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricesControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AdapterPriceToJsonOutputService adapterPriceToJsonOutputService;

    @Before
    public void setUpEach(){
        adapterPriceToJsonOutputService=mock(AdapterPriceToJsonOutputService.class);
    }

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
    void findPriceThenReturnOk(Long brandId, Long productId, String dateString, String startDate, String endDate, BigDecimal price) throws Exception {
        JsonOutputPricesMother jsonOutputPricesMother= new JsonOutputPricesMother();
        JsonOutputPrices jsonOutputPricesExpected = jsonOutputPricesMother.getJsonOutputPrices(productId, brandId,dateString, startDate, endDate, price);

        when(adapterPriceToJsonOutputService.adapterOutputPrices(any(),any(), any())).thenReturn(jsonOutputPricesExpected);

        PricesController pricesController = new PricesController(adapterPriceToJsonOutputService);

        ResponseEntity<JsonOutputPrices> jsonOutput = pricesController.foundPrice(LocalDateFormatter.getDateToFind(dateString), productId, brandId);

        verify(adapterPriceToJsonOutputService).adapterOutputPrices(any(), any(), any());

        assertEquals(HttpStatus.OK, jsonOutput.getStatusCode());

        assertEquals(jsonOutputPricesExpected, jsonOutput.getBody());
    }

}