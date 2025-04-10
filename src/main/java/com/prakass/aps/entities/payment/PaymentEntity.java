package com.prakass.aps.entities.payment;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String guid;

  @ManyToOne
  @JoinColumn(name = "invoice_id")
  private InvoiceEntity invoice;

  private String paymentMethod;
  private double amount;
  private LocalDateTime paymentDate;
  private String transactionId;
}
