package com.jumptospring.example.category;

import com.jumptospring.example.error.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategoryByTitle(String title) {
        Optional<Category> category = this.categoryRepository.findByTitle(title);

        if (category.isEmpty()) {
            category = Optional.ofNullable(create(title));
        }

        if(category.isPresent()){
            return category.get();
        }else{
            throw new DataNotFoundException("question Not Found");
        }

    }

    public Category create(String title) {
        Category category = new Category();
        category.setTitle(title);
        category.setCreateDate(LocalDateTime.now());
        this.categoryRepository.save(category);
        return category;
    }

}
