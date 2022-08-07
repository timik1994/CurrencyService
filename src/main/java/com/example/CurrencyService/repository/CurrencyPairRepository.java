package com.example.CurrencyService.repository;

import com.example.CurrencyService.model.CurrencyPairModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyPairRepository extends JpaRepository<CurrencyPairModel, Integer> {
}
