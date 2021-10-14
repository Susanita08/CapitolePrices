package com.capitole.prices.domain.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationMessageTest {

    @ParameterizedTest
    @EnumSource(ApplicationMessage.class)
    void shouldGetAcquirerRigth(ApplicationMessage input){
        ApplicationMessage amFromCode = ApplicationMessage.fromCode(input.getStrCode());
        ApplicationMessage amFromId = ApplicationMessage.fromCode(input.getCode());
        if(input.getCode()==99){
            assertEquals(input.getStrCode(), amFromCode.getStrCode());
            assertEquals(input.getCode(), amFromId.getCode());
            return;
        }

        assertEquals(input, amFromCode);
        assertEquals(input, amFromId);

    }

}