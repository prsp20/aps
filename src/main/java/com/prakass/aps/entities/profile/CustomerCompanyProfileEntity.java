package com.prakass.aps.entities.profile;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_company_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCompanyProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guid;
    private String companyName;
    private String companyIdentifier;
    private String companyContactEmail;
    private String companyContactPhone;
    private String companyContactFirstName;
    private String companyContactSurname;
    private String companyWebsite;
    private String companyLogo;
    private String industry;
    private int numberOfEmployees;

    @ManyToOne
    @JoinColumn(name = "company_address_id")
    private AddressEntity companyAddress;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
