package com.example.nexus.service;

import java.util.List;

public interface CategoryService {
    void seedCategory(String name);
    List<String> getCategories();
}