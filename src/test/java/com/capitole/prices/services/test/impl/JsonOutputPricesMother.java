package com.capitole.prices.services.test.impl;

import com.capitole.prices.domain.dto.Price;
import com.capitole.prices.enums.ApplicationMessage;
import com.capitole.prices.output.objects.JsonOutputPrices;
import com.capitole.prices.output.objects.JsonOutputPrices.Response;
import com.capitole.prices.services.test.JsonOutputServiceTest;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class JsonOutputPricesMother implements JsonOutputServiceTest {

    @Override
    public JsonOutputPrices getJsonOutputPrices(Long productId, Long brandId, String dateString, String startDate, String endDate, BigDecimal price) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        double tax = 0.21;
        return  JsonOutputPrices.builder().productId(productId).brandId(brandId).dateToFound(dateTime)
                .rateToApply(price).tax(tax).finalPrice(price.add(price.multiply(BigDecimal.valueOf(tax))).setScale(2, RoundingMode.HALF_UP)).price(Price.builder().priceListId(1L).startDate(Timestamp.valueOf(startDate))
                .endDate(Timestamp.valueOf(endDate)).priority(0).price(price).brandId(brandId).productId(productId).currencyCode("EUR").build())
                .response(Response.builder().code(ApplicationMessage.SUCCESS.getCode()).message(ApplicationMessage.SUCCESS.getMessage()).strCode(ApplicationMessage.SUCCESS.getStrCode()).build()).build();

    }

    @Override
    public JsonOutputPrices getJsonOutputPricesConstants(Long productId, Long brandId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss");
        LocalDateTime dateTime = LocalDateTime.parse("2020-06-14-10.00.00", formatter);
        double tax = 0.21;
        BigDecimal price=new BigDecimal("35.5");
        return  JsonOutputPrices.builder().productId(productId).brandId(brandId).dateToFound(dateTime)
                .rateToApply(price).tax(tax).finalPrice(price.add(price.multiply(BigDecimal.valueOf(tax))).setScale(2, RoundingMode.HALF_UP)).price(Price.builder().priceListId(1L).startDate(Timestamp.valueOf("2020-06-14 00:00:00.0"))
                        .endDate(Timestamp.valueOf("2020-12-31 23:59:59.0")).priority(0).price(price).brandId(brandId).productId(productId).currencyCode("EUR").build())
                .response(Response.builder().code(ApplicationMessage.SUCCESS.getCode()).message(ApplicationMessage.SUCCESS.getMessage()).strCode(ApplicationMessage.SUCCESS.getStrCode()).build()).build();

    }
}
