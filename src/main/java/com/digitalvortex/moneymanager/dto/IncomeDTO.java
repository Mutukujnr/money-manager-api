package com.digitalvortex.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeDTO {
    private Long id;

    private String name;

    private String icon;

    private LocalDate date;

    private BigDecimal amount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String categoryName;

    private  Long categoryId;
}
