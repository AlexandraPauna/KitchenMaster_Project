package com.example.project.controller;

import com.example.project.model.Category;
import com.example.project.model.Recipe;
import com.example.project.model.User;
import com.example.project.service.CategoryService;
import com.example.project.service.RecipeService;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

public class RecipeController {
    @Autowired
    RecipeService recipeService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;


    private Map<String, Category> categoryCache;

    @RequestMapping(value = "/recipe/add", method = RequestMethod.GET)
    public String newRecipe(Model model) {
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

        List<Category> categories = categoryService.getAllCategories();
        categoryCache = new HashMap<String, Category>();
        for (Category category : categories) {
            categoryCache.put(category.getCategoryId().toString(), category);
        }
        model.addAttribute("categoriesList", categoryService.getAllCategories());

        Recipe recipe = new Recipe();
        model.addAttribute("recipe", recipe);

        return "/recipe/add";
    }

    @RequestMapping(value = "/recipe/add", method = RequestMethod.POST)
    public String savedRecipe(@Valid Recipe recipe, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            if(recipe.getCategories() != null){
                model.addAttribute("categories", recipe.getCategories());
            }
            model.addAttribute("categoriesList", categoryService.getAllCategories());

            return "/recipe/add";
        }
        recipe.setScore((double) 0);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        recipe.setUser(user);
        Calendar cal = Calendar.getInstance();
        Date dateAdded = cal.getTime();
        recipe.setDate(dateAdded);

        Recipe savedRecipe = recipeService.saveRecipe(recipe);

        return "redirect:/recipe/personal";
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) throws Exception {
        binder.registerCustomEditor(Set.class, "categories", new CustomCollectionEditor(Set.class) {
            protected Object convertElement(Object element) {
                if (element instanceof Category) {
                    System.out.println("Converting from Category to Category: " + element);
                    return element;
                }
                if (element instanceof String) {
                    Category category = categoryCache.get(element);
                    System.out.println("Looking up category for id " + element + ": " + category);
                    return category;
                }
                System.out.println("Don't know what to do with: " + element);
                return null;
            }
        });
    }

    @GetMapping("/recipe/show/{id}")
    public String showRecipe(@PathVariable String id, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if(auth!=null)
        {
            user = userService.findUserByUserName(auth.getName());
        }
        if(user != null){
            model.addAttribute("loggedUser", user);
            model.addAttribute("isAuth", "true");
            String role = user.getRoles().stream().findFirst().get().getRole().toUpperCase();
            model.addAttribute("role", role);
        }
        else{
            model.addAttribute("isAuth", "false");
        }

        model.addAttribute("recipe", recipeService.findRecipeById(Integer.valueOf(id)));

        return "recipe/show";
    }
}
