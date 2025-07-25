package com.digitalvortex.moneymanager.repository;

import com.digitalvortex.moneymanager.model.Expense;
import com.digitalvortex.moneymanager.model.Income;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByProfileIdOrderByDateDesc(Long profileId);

    List<Income> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.profile.id = :incomeId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long incomeId);

    List<Income> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String name,
            Sort sort
    );

    List<Income> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
