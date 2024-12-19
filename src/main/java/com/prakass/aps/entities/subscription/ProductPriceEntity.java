package com.prakass.aps.entities.subscription;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_price")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String guid;
  private SubscriptionBasis subscriptionBasis;
  private BigDecimal price;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private ProductEntity product;
}
