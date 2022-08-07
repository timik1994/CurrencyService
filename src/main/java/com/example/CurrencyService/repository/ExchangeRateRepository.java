package com.example.CurrencyService.repository;

import com.example.CurrencyService.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    @Query(value = "SELECT rate_value FROM exchange_rate WHERE currency_pair_id = :currency_pair_id", nativeQuery = true)
    Float getExchangeRateById(@Param("currency_pair_id") Integer currencyPairId);
}
