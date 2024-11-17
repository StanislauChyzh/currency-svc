package com.test.currencysvc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@SecondaryTable(name = "exchange_rate")
public class ExchangeRate {

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "rate", nullable = false)
  private Double rate;
}
