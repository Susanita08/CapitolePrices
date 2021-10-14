package com.capitole.prices.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
@ToString
public enum ApplicationMessage {

  SUCCESS(0, "Successfully", "00"),

  INVALIDATE_PARAMETERS(2, "Invalidate parameters", "02"),

  BAD_FORMAT(6, "Bad Format", "06"),

  TIMEOUT(99, "Timeout error", "99"),

  UNEXPECTED(99, "Unexpected error", "99");

  private static final long serialVersionUID = 1L;

  private Integer code;
  private String message;
  private String strCode;

  public static ApplicationMessage fromCode(Integer code) {
    return Arrays.stream(values()).filter(applicationMessage -> Objects.equals(code, applicationMessage.getCode())).findAny().orElse(UNEXPECTED);
  }

  public static ApplicationMessage fromCode(String strCode) {
    return Arrays.stream(values()).filter(applicationMessage -> Objects.equals(strCode, applicationMessage.getStrCode())).findAny()
        .orElse(UNEXPECTED);
  }
}
