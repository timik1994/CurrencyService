package com.example.CurrencyService.repository;

import com.example.CurrencyService.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query(value = "SELECT FIRST 1 SKIP 0 er.* FROM exchange_rate er WHERE er.currency_pair_id = :currency_pair_id ORDER BY er.RATE_DATE DESC ", nativeQuery = true)
    ExchangeRate getExchangeRateById(@Param("currency_pair_id") Integer currencyPairId);

}


