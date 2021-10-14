package com.capitole.prices.services.test.impl;

import com.capitole.prices.domain.dto.Price;
import com.capitole.prices.api.response.JsonOutputPrices;
import com.capitole.prices.api.response.JsonOutputPrices.Response;
import com.capitole.prices.services.test.JsonOutputServiceTest;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.capitole.prices.domain.enums.ApplicationMessage.SUCCESS;

@NoArgsConstructor
public class JsonOutputPricesMother implements JsonOutputServiceTest {

    @Override
    public JsonOutputPrices getJsonOutputPrices(Long productId, Long brandId, String dateString, String startDate, String endDate, BigDecimal rateToApply) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        Timestamp start = Timestamp.valueOf(startDate);
        Timestamp end = Timestamp.valueOf(endDate);
        Price price= Price.builder().priceListId(1L).startDate(start).endDate(end).priority(0).price(rateToApply)
                .brandId(brandId).productId(productId).currencyCode("EUR").build();
        double tax = 0.21;
        BigDecimal finalPrice=rateToApply.add(rateToApply.multiply(BigDecimal.valueOf(tax))).setScale(2, RoundingMode.HALF_UP);
        return  JsonOutputPrices.builder()
                .productId(productId)
                .brandId(brandId)
                .dateToFound(dateTime)
                .rateToApply(rateToApply)
                .tax(tax)
                .finalPrice(finalPrice)
                .price(price)
                .response(Response.builder().code(SUCCESS.getCode()).message(SUCCESS.getMessage()).strCode(SUCCESS.getStrCode()).build())
                .build();

    }

    @Override
    public JsonOutputPrices getJsonOutputPricesConstants(Long productId, Long brandId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss");
        LocalDateTime dateTime = LocalDateTime.parse("2020-06-14-10.00.00", formatter);
        BigDecimal rateToApply=new BigDecimal("35.5");
        double tax = 0.21;
        BigDecimal finalPrice=rateToApply.add(rateToApply.multiply(BigDecimal.valueOf(tax))).setScale(2, RoundingMode.HALF_UP);
        Timestamp startDate=Timestamp.valueOf("2020-06-14 00:00:00.0");
        Timestamp endDate=Timestamp.valueOf("2020-12-31 23:59:59.0");
        return  JsonOutputPrices.builder().productId(productId)
                .brandId(brandId)
                .dateToFound(dateTime)
                .rateToApply(rateToApply)
                .tax(tax)
                .finalPrice(finalPrice)
                .price(getPrice(startDate, endDate, rateToApply,productId,brandId))
                .response(Response.builder().code(SUCCESS.getCode()).message(SUCCESS.getMessage()).strCode(SUCCESS.getStrCode()).build()).build();

    }

    private Price getPrice(Timestamp startDate, Timestamp endDate, BigDecimal rateToApply, Long productId, Long brandId){
        return Price.builder().priceListId(1L).startDate(startDate).endDate(endDate).priority(0).price(rateToApply)
                .brandId(brandId).productId(productId).currencyCode("EUR").build();
    }
}
