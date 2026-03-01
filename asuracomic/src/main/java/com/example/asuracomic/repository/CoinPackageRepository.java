package com.example.asuracomic.repository;

import com.example.asuracomic.entity.CoinPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoinPackageRepository extends JpaRepository<CoinPackage, Long> {
    List<CoinPackage> findByActiveTrue();
}