package com.capitole.prices.api.rest;

import com.capitole.prices.domain.enums.ApplicationMessage;
import com.capitole.prices.api.response.JsonOutputPrices;
import com.capitole.prices.domain.ports.primary.AdapterPriceToJsonOutputService;
import com.capitole.prices.validators.anotations.ConsistentDateParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.capitole.prices.utils.ConstantsUtils.*;
import static java.util.Optional.ofNullable;

@RestController
@Validated
@RequestMapping(PATH_SEPARATOR + PRICES + PATH_SEPARATOR + API_VERSION)
@Api(value = "/api-prices")
public class PricesController {
    private static final Log log = LogFactory.getLog(PricesController.class);
    private final static Map<String, HttpStatus> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(ApplicationMessage.INVALIDATE_PARAMETERS.getStrCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        STATUS_MAP.put(ApplicationMessage.UNEXPECTED.getStrCode(), HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(ApplicationMessage.SUCCESS.getStrCode(), HttpStatus.OK);
    }

    @Autowired
    private final AdapterPriceToJsonOutputService adapterPriceToJsonOutputService;

    public PricesController(AdapterPriceToJsonOutputService adapterPriceToJsonOutputService) {
        this.adapterPriceToJsonOutputService=adapterPriceToJsonOutputService;
    }

    @ApiOperation(value = "Find price by date valid, productId and brandId in the system")
    @ApiResponses( {@ApiResponse(code = 200, message = "Find price valid in the system")})
    @GetMapping(path = PATH_SEPARATOR + FIND_PRICE + PATH_SEPARATOR + PRODUCT_ID + PATH_SEPARATOR + BRAND_ID ,
            consumes=MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @ConsistentDateParameters
    public ResponseEntity<JsonOutputPrices> foundPrice(@Valid @RequestParam(name = "dateFound", required = true) LocalDateTime dateFound,
                                                       @PathVariable Long productId, @PathVariable Long brandId) {
        log.info("Find price valid.");
        JsonOutputPrices jsonOutputPrices =adapterPriceToJsonOutputService.adapterOutputPrices(dateFound, productId, brandId);
        return ResponseEntity.status(getHttpStatusFromResponseCode(jsonOutputPrices.getResponse().getStrCode())).
                body(jsonOutputPrices);
    }

    private HttpStatus getHttpStatusFromResponseCode(String responseCode){
        log.info("Get HttpStatus of response in PriceController: "+responseCode);
        return ofNullable(STATUS_MAP.get(responseCode)).orElse(HttpStatus.OK);
    }
}
