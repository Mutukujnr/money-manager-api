package com.digitalvortex.moneymanager.service;

import com.digitalvortex.moneymanager.dto.ExpenseDTO;
import com.digitalvortex.moneymanager.model.Category;
import com.digitalvortex.moneymanager.model.Expense;
import com.digitalvortex.moneymanager.model.Profile;
import com.digitalvortex.moneymanager.repository.CategoryRepository;
import com.digitalvortex.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;

    private final ExpenseRepository expenseRepository;

    private final ProfileService profileService;



    public ExpenseDTO addEXpense(ExpenseDTO expenseDTO){
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(()-> new RuntimeException("category not found"));

        Expense entity = toEntity(expenseDTO, profile, category);

       Expense instance = expenseRepository.save(entity);

       return toDTO(instance);

    }
    
    //retrieve expenses based on the satart and end date
    public List<ExpenseDTO> expensesForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
       LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);

        return expenses.stream().map(this::toDTO).toList();
    }

    //delete expense for current user

    public void deleteExpenseForCurrentUser(Long expenseId){
     Profile profile =  profileService.getCurrentProfile();
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("expense not found"));

        if(!expense.getProfile().getId().equals(profile.getId())){

            throw new RuntimeException("unauthorized to delete this expense");
        }

        expenseRepository.delete(expense);

    }

    public List<ExpenseDTO> latestFiveExpensesForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();

        List<Expense> expenses = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());

        return expenses.stream().map(this::toDTO).toList();
    }

    //Get total amount of expenses of curret user
    public BigDecimal getTotalExpensesForCurrentUser(){
        Profile profile = profileService.getCurrentProfile();

        BigDecimal totalExpenseByProfileId = expenseRepository.findTotalExpenseByProfileId(profile.getId());

        return totalExpenseByProfileId != null ? totalExpenseByProfileId : BigDecimal.ZERO;
    }

    //filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyWord, Sort sort){
        Profile profile = profileService.getCurrentProfile();

      List<Expense> expenses =  expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyWord,sort);

     return expenses.stream().map(this::toDTO).toList();
    }

    //notifications
    public List<ExpenseDTO> getExpencesForUserForDate(Long profileId, LocalDate date){
        //Profile profile = profileService.getCurrentProfile();

       List<Expense> list = expenseRepository.findByProfileIdAndDate(profileId,date);

       return list.stream().map(this::toDTO).toList();

    }


    //helper methods

    private Expense toEntity(ExpenseDTO expenseDTO, Profile profile, Category category){
        return Expense.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profile)
                .category(category)
                .build();

    }

    private ExpenseDTO toDTO(Expense expense){
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId(): null)
                .categoryName(expense.getCategory()!= null ? expense.getCategory().getName() :  null)
                .amount(expense.getAmount())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();

    }
}
