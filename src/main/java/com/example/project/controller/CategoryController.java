package com.example.project.controller;

import com.example.project.model.Category;
import com.example.project.model.User;
import com.example.project.service.CategoryService;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/category/new", method = RequestMethod.GET)
    public String newCategory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        if(user != null){
            model.addAttribute("loggedUser", user);
            model.addAttribute("isAuth", "true");
            String role = user.getRoles().stream().findFirst().get().getRole().toUpperCase();
            model.addAttribute("role", role);
        }
        else{
            model.addAttribute("isAuth", "false");
        }
        Category category = new Category();
        model.addAttribute("category", category);
        return "/category/new";
    }

    @RequestMapping(value = "/category/new", method = RequestMethod.POST)
    public String savedCategory(@Valid Category category, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/category/new";
        }

        Category savedCategory = categoryService.saveCategory(category);
        return "/category/index";
    }

    @GetMapping("/category/show/{id}")
    public String showCategory(@PathVariable String id, Model model,
                               @RequestParam(defaultValue = "1", required = false) Integer pageNumber,
                               @RequestParam(value="sortKey", defaultValue="date") String sortKey){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        model.addAttribute("loggedUser", user);
        if(user != null){
            model.addAttribute("isAuth", "true");
            String role = user.getRoles().stream().findFirst().get().getRole().toUpperCase();
            model.addAttribute("role", role);
        }
        else{
            model.addAttribute("isAuth", "false");
            model.addAttribute("role", null);
        }

        model.addAttribute("category", categoryService.findCategoryById(Integer.valueOf(id)));
//        List<Recipe> recipes = new ArrayList<>(categoryService.findCategoryById(Integer.valueOf(id)).getRecipes());
//        model.addAttribute("nrOfRecipes",recipes.size());

//        Page<Recipe> pages = recipeService.getAllRecipesByCategoryPage(Integer.valueOf(id), pageNumber, sortKey);
//        model.addAttribute("recipes", pages);
        model.addAttribute("currentPage", pageNumber);
//        model.addAttribute("sortKey", sortKey);

        //TO DO: sa se elimine categoria curenta din lista
//        List<Category> allCategories = categoryService.getAllCategories();
//        model.addAttribute("allCategories", allCategories);

        return "category/show";
    }
}
