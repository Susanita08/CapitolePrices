package com.capitole.prices.domain.services;

import com.capitole.prices.domain.dto.Price;
import com.capitole.prices.domain.exceptions.DateException;
import com.capitole.prices.domain.ports.secundary.PricesServices;
import com.capitole.prices.infrastructure.repository.PricesRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.capitole.prices.utils.ConstantsUtils.FORMAT_INVALIDATE;

@Service
public class PricesServiceImpl implements PricesServices {
    private static final Log log = LogFactory.getLog(PricesServiceImpl.class);

    @Autowired
    private final PricesRepository pricesRepository;

    public PricesServiceImpl(PricesRepository pricesRepository) {
        this.pricesRepository = pricesRepository;
    }

    @Override
    public Price searchPrice(LocalDateTime dateFound, Long productId, Long brandId) {
        log.info("Found price to apply in the date: "+dateFound+" ,with of productId: "+productId+" , of the brand: "+brandId);
        Timestamp timeDB= Optional.of(Timestamp.valueOf(dateFound)).orElseThrow(()-> new DateException(FORMAT_INVALIDATE));
        return pricesRepository.findByProductIdAndBrandIdAndDateBetweenStartDateAndEndDate(timeDB, productId, brandId);
    }
}
