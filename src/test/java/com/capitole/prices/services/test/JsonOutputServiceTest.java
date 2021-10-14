package com.capitole.prices.services.test;

import com.capitole.prices.api.response.JsonOutputPrices;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Profile("test")
public interface JsonOutputServiceTest {
    JsonOutputPrices getJsonOutputPrices(Long productId, Long brandId, String dateString, String startDate, String endDate, BigDecimal price);
    JsonOutputPrices getJsonOutputPricesConstants(Long productId, Long brandId);

}
