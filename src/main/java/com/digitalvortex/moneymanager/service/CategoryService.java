package com.digitalvortex.moneymanager.service;

import com.digitalvortex.moneymanager.dto.CategoryDTO;
import com.digitalvortex.moneymanager.model.Category;
import com.digitalvortex.moneymanager.model.Profile;
import com.digitalvortex.moneymanager.repository.CategoryRepository;
import com.digitalvortex.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ProfileRepository profileRepository;

    private final ProfileService profileService;

    //helper methods
    private Category toEntity(CategoryDTO categoryDTO, Profile profile){
        return Category.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .profileId(category.getProfile() != null ? category.getProfile().getId(): null)
                .name(category.getName())
                .icon(category.getIcon())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .type(category.getType())
                .build();

    }

    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        Profile currentProfile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(),currentProfile.getId())){
            throw new RuntimeException("Category with this name already exists");
        }

        Category entity = toEntity(categoryDTO, currentProfile);
        Category savedCategory = categoryRepository.save(entity);

        return toDTO(savedCategory);
    }

    public List<CategoryDTO> getCategoriesForCurrentUser(){
        Profile currentProfile = profileService.getCurrentProfile();

        List<Category> categories = categoryRepository.findByProfileId(currentProfile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTYpeForCurrentUser(String type){
        Profile currentProfile = profileService.getCurrentProfile();

     List<Category> categories =  categoryRepository.findByTypeAndProfileId(type, currentProfile.getId());

     return categories.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO){
        Profile currentProfile = profileService.getCurrentProfile();
      Category existingCategory =  categoryRepository.findByIdAndProfileId(categoryId,currentProfile.getId()).orElseThrow(()-> new RuntimeException("Category not found"));

      existingCategory.setName(categoryDTO.getName());
      existingCategory.setIcon(categoryDTO.getIcon());
      existingCategory.setType(categoryDTO.getType());

        Category save = categoryRepository.save(existingCategory);
        return toDTO(save);
    }
}
