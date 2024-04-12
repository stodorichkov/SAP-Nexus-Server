package com.example.nexus.service;

import com.example.nexus.model.entity.Category;
import com.example.nexus.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTests {
    private static Category category;

    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeAll
    static void setUp() {
        category = new Category();
        category.setName("Category");
    }

    @Test
    void seedCategory_categoryAlreadyExist_expectNotSave() {
        when(this.categoryRepository.findByName(eq(category.getName()))).thenReturn(Optional.of(category));

        this.categoryService.seedCategory(category.getName());

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void seedCategory_categoryNotExist_expectSave() {
        this.categoryService.seedCategory(category.getName());

        verify(this.categoryRepository, times(1))
                .save(argThat(c -> c.equals(category)));
    }

    @Test
    void getCategories_expectList() {
        when(this.categoryRepository.findAll()).thenReturn(List.of(category));

        final var result = this.categoryService.getCategories();

        assertEquals(List.of(category.getName()), result);
    }
}