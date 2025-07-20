package com.digitalvortex.moneymanager.dto;

import com.digitalvortex.moneymanager.model.Category;
import com.digitalvortex.moneymanager.model.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    private Long id;

    private String name;

    private String icon;

    private LocalDate date;

    private BigDecimal amount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String categoryName;

    private  Long categoryId;

   // private Long profileId;
}
