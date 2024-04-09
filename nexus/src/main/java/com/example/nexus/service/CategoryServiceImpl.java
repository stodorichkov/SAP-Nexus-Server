package com.example.nexus.service;

import com.example.nexus.model.entity.Category;
import com.example.nexus.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public void seedCategory(String name) {
        if(this.categoryRepository.findByName(name).isPresent()) {
            return;
        }

        final var category = new Category();
        category.setName(name);

        this.categoryRepository.save(category);
    }

    @Override
    public List<String> getCategories() {
        return this.categoryRepository
                .findAll()
                .stream()
                .map(Category::getName)
                .toList();
    }
}