package com.capitole.prices.domain.services;

import com.capitole.prices.domain.ports.secundary.PricesServices;
import com.capitole.prices.infrastructure.config.SelfConfiguration;
import com.capitole.prices.domain.enums.ApplicationMessage;
import com.capitole.prices.api.response.JsonOutputPrices;
import com.capitole.prices.services.test.impl.JsonOutputPricesMother;
import com.capitole.prices.utils.LocalDateFormatter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class AdapterPriceToJsonOutputImplTest {

    @Mock
    private SelfConfiguration selfConfiguration;

    @Mock
    private PricesServices pricesServices;

    public static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(1L, 35455L, "2020-06-14-10.00.00", "2020-06-14 00:00:00.0", "2020-12-31 23:59:59.0", new BigDecimal("35.5")),
                Arguments.of(1L, 35455L, "2020-06-14-16.00.00", "2020-06-14 15:00:00.0", "2020-06-14 18:30:00.0", new BigDecimal("25.45")),
                Arguments.of(1L, 35455L, "2020-06-14-21.00.00", "2020-06-14 00:00:00.0", "2020-12-31 23:59:59.0", new BigDecimal("35.5")),
                Arguments.of(1L, 35455L, "2020-06-15-10.00.00", "2020-06-15 00:00:00.0", "2020-06-15 11:00:00.0", new BigDecimal("30.5")),
                Arguments.of(1L, 35455L, "2020-06-16-21.00.00", "2020-06-15 16:00:00.0", "2020-12-31 23:59:59.0", new BigDecimal("38.95"))
        );
    }

    public static Stream<Arguments> testCasesWithError() {
        return Stream.of(
                Arguments.of(1L, null, "2020-06-14-10.00.00"), Arguments.of(1L, 35455L, null), Arguments.of(null, 35455L, "2020-06-14-21.00.00"),
                Arguments.of(0L, 35455L, "2020-06-16-21.00.00"), Arguments.of(null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void givenPriceWithoutErrors(Long brandId, Long productId, String dateString, String startDate, String endDate, BigDecimal price) throws Exception {
        pricesServices = mock(PricesServices.class);
        selfConfiguration = mock(SelfConfiguration.class);
        JsonOutputPricesMother jsonOutputPricesMother = new JsonOutputPricesMother();
        JsonOutputPrices jsonOutputPricesExpected = jsonOutputPricesMother.getJsonOutputPrices(productId, brandId, dateString, startDate, endDate, price);
        LocalDateTime localDateTime = jsonOutputPricesExpected.getDateToFound();
        when(pricesServices.searchPrice(any(), any(), any())).thenReturn(jsonOutputPricesExpected.getPrice());
        when(selfConfiguration.getTax()).thenReturn(0.21);

        AdapterPriceToJsonOutputImpl adapterPriceToJsonOutputService = new AdapterPriceToJsonOutputImpl(selfConfiguration, pricesServices);

        final JsonOutputPrices result = adapterPriceToJsonOutputService.adapterOutputPrices(localDateTime, productId, brandId);

        verify(pricesServices).searchPrice(any(), any(), any());

        assertEquals(jsonOutputPricesExpected.getPrice(), result.getPrice());
        assertEquals(jsonOutputPricesExpected.getFinalPrice(), result.getFinalPrice());
        assertEquals(jsonOutputPricesExpected.getResponse().getMessage(), result.getResponse().getMessage());
    }

    @ParameterizedTest
    @MethodSource("testCasesWithError")
    void givenPriceWithErrors(Long brandId, Long productId, String dateString) {
        pricesServices = mock(PricesServices.class);
        selfConfiguration = mock(SelfConfiguration.class);
        LocalDateTime dateTime = null;

        if (nonNull(dateString)) {
            //"yyyy-MM-dd-HH.mm.ss" format solicited in the test capitole
            dateTime = LocalDateFormatter.getDateToFind(dateString);
        }

        when(selfConfiguration.getTax()).thenReturn(0.21);

        AdapterPriceToJsonOutputImpl pricesService = new AdapterPriceToJsonOutputImpl(selfConfiguration, pricesServices);

        final JsonOutputPrices result = pricesService.adapterOutputPrices(dateTime, productId, brandId);

        assertEquals(ApplicationMessage.UNEXPECTED.getCode(), result.getResponse().getCode());
        assertEquals(ApplicationMessage.UNEXPECTED.getMessage(), result.getResponse().getMessage());
        assertEquals(ApplicationMessage.UNEXPECTED.getStrCode(), result.getResponse().getStrCode());

    }
}