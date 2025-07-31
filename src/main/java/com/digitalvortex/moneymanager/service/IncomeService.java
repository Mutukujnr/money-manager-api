package com.digitalvortex.moneymanager.service;

import com.digitalvortex.moneymanager.dto.ExpenseDTO;
import com.digitalvortex.moneymanager.dto.IncomeDTO;
import com.digitalvortex.moneymanager.model.Category;
import com.digitalvortex.moneymanager.model.Expense;
import com.digitalvortex.moneymanager.model.Income;
import com.digitalvortex.moneymanager.model.Profile;
import com.digitalvortex.moneymanager.repository.CategoryRepository;
import com.digitalvortex.moneymanager.repository.ExpenseRepository;
import com.digitalvortex.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;

    private final IncomeRepository incomeRepository;

    private final ProfileService profileService;



    public IncomeDTO addIncome(IncomeDTO incomeDTO){
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(()-> new RuntimeException("category not found"));

        Income entity = toEntity(incomeDTO, profile, category);

        Income instance = incomeRepository.save(entity);

        return toDTO(instance);

    }

    //retrieve incomes based on the satart and end date
    public List<IncomeDTO> incomesForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<Income> incomes = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);

        return incomes.stream().map(this::toDTO).toList();
    }

    public void deleteIncomeForCurrentUser(Long incomeId){
        Profile profile =  profileService.getCurrentProfile();
        Income income = incomeRepository.findById(incomeId).orElseThrow(() -> new RuntimeException("income not found"));

        if(!income.getProfile().getId().equals(profile.getId())){

            throw new RuntimeException("unauthorized to delete this income");
        }

        incomeRepository.delete(income);

    }


    public List<IncomeDTO> latestFiveIncomesForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();

        List<Income> incomes = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());

        return incomes.stream().map(this::toDTO).toList();
    }

    //Get total amount of expenses of curret user
    public BigDecimal getTotalIncomeForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();

        BigDecimal totalExpenseByProfileId = incomeRepository.findTotalIncomeByProfileId(profile.getId());

        return totalExpenseByProfileId != null ? totalExpenseByProfileId : BigDecimal.ZERO;
    }

    //filter incomes
    public List<IncomeDTO> filterIncome(LocalDate startDate, LocalDate endDate, String keyWord, Sort sort){
        Profile profile = profileService.getCurrentProfile();

        List<Income> expenses =  incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyWord,sort);

        return expenses.stream().map(this::toDTO).toList();
    }
    //helper methods

    private Income toEntity(IncomeDTO incomeDTO, Profile profile, Category category){
        return Income.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profile)
                .category(category)
                .build();

    }

    private IncomeDTO toDTO(Income income) {
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .amount(income.getAmount())
                .date(income.getDate())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : null)
                .amount(income.getAmount())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }


}
