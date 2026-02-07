package com.example.assistio.controller;
import com.example.assistio.model.Category;
import com.example.assistio.service.CategoryService;
import com.example.assistio.service.UserService;
import com.example.assistio.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final UserService userService;
    @Autowired
    private CategoryService categoryService;

    CategoryController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(
        
    ) {
        User user = getCurrentUser();
        try {
            List<Category> categories = categoryService.getAllCategories(user);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            if (e.toString().contains("Access Denied")){
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
        User user = getCurrentUser();
        try {
            Category category = categoryService.getCategoryById(categoryId, user);
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/short-name/{shortName}")
    public ResponseEntity<Category> getCategoryByShortName(@PathVariable String shortName) {
        User user = getCurrentUser();
        try {
            Category category = categoryService.getCategoryByShortName(shortName, user);
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(userDetails.getUsername());
    }
    
}
