package com.capitole.prices.domain.services;

import com.capitole.prices.api.response.JsonOutputPrices;
import com.capitole.prices.domain.dto.Price;
import com.capitole.prices.domain.enums.ApplicationMessage;
import com.capitole.prices.domain.ports.primary.AdapterPriceToJsonOutputService;
import com.capitole.prices.domain.ports.secundary.PricesServices;
import com.capitole.prices.infrastructure.config.SelfConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static com.capitole.prices.utils.ConstantsUtils.FAILED_QUERY;
import static com.capitole.prices.utils.ConstantsUtils.FORMAT_INVALIDATE;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Service
public class AdapterPriceToJsonOutputImpl implements AdapterPriceToJsonOutputService {

    @Autowired
    private final SelfConfiguration selfConfiguration;

    @Autowired
    private final PricesServices pricesServices;

    public AdapterPriceToJsonOutputImpl(SelfConfiguration selfConfiguration, PricesServices pricesServices) {
        this.selfConfiguration = selfConfiguration;
        this.pricesServices=pricesServices;
    }

    @Override
    public JsonOutputPrices adapterOutputPrices(LocalDateTime dateFound, Long productId, Long brandId) {
        if (isNull(dateFound) || isNull(productId) || isNull(brandId)) {
            return getBadResponse();
        }
        Price price = pricesServices.searchPrice(dateFound,productId,brandId);

        return ofNullable(price).map(p -> setResponseOutputPrice(p,dateFound))
                .orElse(JsonOutputPrices.builder().response(setResponsePrice(ApplicationMessage.UNEXPECTED, FAILED_QUERY)).build());
    }

    private JsonOutputPrices setResponseOutputPrice(Price price, LocalDateTime timeDB){
        double tax=selfConfiguration.getTax();
        JsonOutputPrices.Response response = setResponsePrice(ApplicationMessage.SUCCESS, "");
        JsonOutputPrices jsonOutputPrices = new JsonOutputPrices();
        jsonOutputPrices.setProductId(price.getProductId());
        jsonOutputPrices.setBrandId(price.getBrandId());
        jsonOutputPrices.setDateToFound(timeDB);
        jsonOutputPrices.setPrice(price);
        jsonOutputPrices.setRateToApply(price.getPrice());
        jsonOutputPrices.setTax(tax);
        jsonOutputPrices.setFinalPrice(calculateFinalPrice(price.getPrice()));
        jsonOutputPrices.setResponse(response);
        return jsonOutputPrices;
    }

    @Override
    public BigDecimal calculateFinalPrice(BigDecimal price) {
        return price.add(price.multiply(BigDecimal.valueOf(selfConfiguration.getTax()))).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public JsonOutputPrices.Response setResponsePrice(ApplicationMessage application, String description) {
        return JsonOutputPrices.Response.builder().
                code(application.getCode())
                .message(application.getMessage())
                .strCode(application.getStrCode())
                .description(description).build();
    }

    public JsonOutputPrices getBadResponse() {
        return JsonOutputPrices.builder().response(setResponsePrice(ApplicationMessage.UNEXPECTED, FORMAT_INVALIDATE)).build();
    }
}
