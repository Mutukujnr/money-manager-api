package com.digitalvortex.moneymanager.dto;

import com.digitalvortex.moneymanager.model.Profile;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;

    private Long profileId;

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String type;

    private String icon;


}
