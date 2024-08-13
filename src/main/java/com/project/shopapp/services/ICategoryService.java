package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category createCategory(CategoryDTO category);
    Category updateCategory(long id,CategoryDTO category);
    void deleteCategory(long id);
}
