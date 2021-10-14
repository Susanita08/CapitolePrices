package com.capitole.prices.domain.ports.secundary;

import com.capitole.prices.domain.dto.Price;


import java.time.LocalDateTime;

public interface PricesServices {
    Price searchPrice(LocalDateTime dateFound, Long productId, Long brandId);
}
