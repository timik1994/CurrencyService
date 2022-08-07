package com.example.CurrencyService.repository;

import com.example.CurrencyService.model.CurrencyPairModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyPairRepository extends JpaRepository<CurrencyPairModel, Integer> {

    @Query(value = "SELECT * FROM CURRENCY_PAIR cp WHERE cp.BASE_CHAR_CODE=:baseCharCode AND cp.QUOTED_CHAR_CODE=:quotedCharCode", nativeQuery = true)
    CurrencyPairModel getCurrencyPairByPairName(@Param(value = "baseCharCode") String baseCharCode, @Param(value = "quotedCharCode") String quotedCharCode);
}
