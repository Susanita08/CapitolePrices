package com.capitole.prices.api.response;

import com.capitole.prices.domain.dto.Price;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonOutputPrices implements Serializable {
    private static final long serialVersionUID = -6334581547411113963L;

    @NotNull
    private Long productId;

    @NotNull
    private Long brandId;

    @NotNull
    private LocalDateTime dateToFound;

    @NotNull
    private BigDecimal rateToApply;

    @NotNull
    @Valid
    @JsonIgnoreProperties(value = {
            "priceListId", "brandId", "productId", "startDate", "endDate", "price"
    }, ignoreUnknown = true)
    private Price price;

    private double tax;

    private BigDecimal finalPrice;


    @Valid
    @NotNull
    private Response response;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response implements Serializable {
        private static final long serialVersionUID = 3538641147032958369L;

        @Min(value=0)
        @Digits(integer = 2, fraction =0)
        private Integer code;

        private String message;

        private String strCode;

        private String description;
    }
}
