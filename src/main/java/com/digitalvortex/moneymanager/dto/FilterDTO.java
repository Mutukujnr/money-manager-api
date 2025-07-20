package com.digitalvortex.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterDTO {

    private String type;

    private LocalDate startDate;

    private LocalDate endDate;

    private String keyWord;

    private String sortField;

    private String sortOrder;
}
