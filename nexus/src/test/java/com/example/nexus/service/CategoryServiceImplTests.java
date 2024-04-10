package com.example.nexus.service;

import com.example.nexus.model.entity.Category;
import com.example.nexus.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTests {
    private static String categoryName;
    private static List<String> names;
    private static List<Category> categories;

    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeAll
    static void setUp() {
        categoryName = "New category";

        names = List.of("C1", "C2", "C3");

        categories = new ArrayList<>();
        names.forEach(n -> {
            final var category = new Category();
            category.setName(n);
            categories.add(category);
        });
    }

    @Test
    void seedCategory_categoryNotExist_expectSave() {
        this.categoryService.seedCategory(categoryName);

        verify(this.categoryRepository, times(1))
                .save(argThat(role -> role.getName().equals(categoryName)));
    }

    @Test
    void seedCategory_categoryAlreadyExist_expectNotSave() {
        when(this.categoryRepository.findByName(categoryName)).thenReturn(Optional.of(new Category()));

        this.categoryService.seedCategory(categoryName);

        verify(this.categoryRepository, never()).save(any());
    }

    @Test
    void getCategories_expectList() {
        when(this.categoryRepository.findAll()).thenReturn(categories);

        final var result = this.categoryService.getCategories();

        assertEquals(names, result);
    }
}