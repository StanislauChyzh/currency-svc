package com.test.currencysvc.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.isNull;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.util.ProxyUtils;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "currency")
@ToString(exclude = "rates")
public class CurrencyModel {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(name = "code", unique = true)
  private String code;

  @ElementCollection
  @CollectionTable(name = "exchange_rate", joinColumns = @JoinColumn(name = "currency_id"))
  private List<ExchangeRate> rates = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (isNull(o) || ProxyUtils.getUserClass(this) != ProxyUtils.getUserClass(o)) return false;
    final var currencyModel = (CurrencyModel) o;
    return Objects.equals(id, currencyModel.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
