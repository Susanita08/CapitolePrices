package com.capitole.prices.domain.ports.primary;

import com.capitole.prices.api.response.JsonOutputPrices;
import com.capitole.prices.domain.enums.ApplicationMessage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AdapterPriceToJsonOutputService {
    JsonOutputPrices adapterOutputPrices(LocalDateTime dateFound, Long productId, Long brandId);
    BigDecimal calculateFinalPrice(BigDecimal price);
    JsonOutputPrices.Response setResponsePrice(ApplicationMessage application, String description);
}
