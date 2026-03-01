package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "coin_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // VD: Gói 50k

    private BigDecimal price; // tiền VNĐ

    private Integer coin; // số coin nhận được

    private Boolean active = true;
}
