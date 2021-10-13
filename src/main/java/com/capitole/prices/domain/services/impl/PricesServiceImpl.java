package com.capitole.prices.domain.services.impl;

import com.capitole.prices.api.rest.PricesController;
import com.capitole.prices.config.SelfConfiguration;
import com.capitole.prices.domain.dto.Price;
import com.capitole.prices.domain.repository.PricesRepository;
import com.capitole.prices.domain.services.PricesServices;
import com.capitole.prices.enums.ApplicationMessage;
import com.capitole.prices.exceptions.DateException;
import com.capitole.prices.output.objects.JsonOutputPrices;
import com.capitole.prices.output.objects.JsonOutputPrices.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.capitole.prices.utils.ConstantsUtils.FAILED_QUERY;
import static com.capitole.prices.utils.ConstantsUtils.FORMAT_INVALIDATE;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PricesServiceImpl implements PricesServices {
    private static final Log log = LogFactory.getLog(PricesServiceImpl.class);

    private final PricesRepository pricesRepository;

    @Autowired
    private final SelfConfiguration selfConfiguration;


    public PricesServiceImpl(PricesRepository pricesRepository, SelfConfiguration selfConfiguration) {
        this.pricesRepository = pricesRepository;
        this.selfConfiguration = selfConfiguration;
    }

    private BigDecimal calculateFinalPrice(BigDecimal price) {
        return price.add(price.multiply(BigDecimal.valueOf(selfConfiguration.getTax()))).setScale(2, RoundingMode.HALF_UP);
    }

    public JsonOutputPrices getBadResponse() {
        Response response = JsonOutputPrices.Response.builder()
                .code(ApplicationMessage.UNEXPECTED.getCode())
                .message(ApplicationMessage.UNEXPECTED.getMessage())
                .strCode(ApplicationMessage.UNEXPECTED.getStrCode()).build();
        return JsonOutputPrices.builder().response(response).build();
    }

    @Override
    public JsonOutputPrices searchPrice(LocalDateTime dateFound, Long productId, Long brandId) {
        log.info("Found price to apply in the date: "+dateFound+" ,with of productId: "+productId+" , of the brand: "+brandId);
        if (isNull(dateFound) || isNull(productId) || isNull(brandId)) {
            return getBadResponse();
        }
        Timestamp timeDB= Optional.of(Timestamp.valueOf(dateFound)).orElseThrow(()-> new DateException(FORMAT_INVALIDATE));
        Price price= pricesRepository.findByProductIdAndBrandIdAndDateBetweenStartDateAndEndDate(timeDB, productId, brandId);

        return ofNullable(price).map(p -> JsonOutputPrices.builder().productId(productId)
                        .brandId(brandId).dateToFound(timeDB.toLocalDateTime())
                        .price(p).rateToApply(p.getPrice())
                        .tax(selfConfiguration.getTax())
                        .finalPrice(calculateFinalPrice(p.getPrice()))
                        .response(setResponsePrice(ApplicationMessage.SUCCESS, "")).build())
                .orElse(JsonOutputPrices.builder().response(setResponsePrice(ApplicationMessage.UNEXPECTED, FAILED_QUERY)).build());
    }

    @Override
    public void setLink(JsonOutputPrices jsonOutputPrices, Long brandId, Long productId, LocalDateTime dateFound){
        if(isNull(jsonOutputPrices) || isNull(jsonOutputPrices.getPrice())){
            return;
        }
        jsonOutputPrices.getPrice().add(linkTo(methodOn(PricesController.class)
                .foundPrice(brandId, productId, dateFound)).withSelfRel());
    }

    private Response setResponsePrice(ApplicationMessage aplication, String description){
        return Response.builder().
                        code(aplication.getCode())
                        .message(aplication.getMessage())
                        .strCode(aplication.getStrCode())
                        .description(description).build();
    }
}
