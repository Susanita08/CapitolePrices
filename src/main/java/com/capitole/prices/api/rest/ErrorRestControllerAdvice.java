package com.capitole.prices.api.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;

import com.capitole.prices.enums.ApplicationMessage;
import com.capitole.prices.output.objects.JsonOutputPrices;
import com.capitole.prices.output.objects.JsonOutputPrices.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice(annotations=RestController.class)
public class ErrorRestControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ErrorRestControllerAdvice.class);

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonOutputPrices handleException(Exception ex, HttpServletRequest request) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        logger.info("executing exception handler (REST): ");
        logger.error(sw.toString());
        return JsonOutputPrices.builder().response(Response.builder()
                .code(ApplicationMessage.UNEXPECTED.getCode())
                .message(ApplicationMessage.UNEXPECTED.getMessage())
                .description(ex.getMessage()).build()).build();

    }

}