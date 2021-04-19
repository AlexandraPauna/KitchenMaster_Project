package com.example.project.controller;

import com.example.project.model.Category;
import com.example.project.model.Recipe;
import com.example.project.model.User;
import com.example.project.service.CategoryService;
import com.example.project.service.RecipeService;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    RecipeService recipeService;

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
        return "redirect:/category/index";
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
        List<Recipe> recipes = new ArrayList<>(categoryService.findCategoryById(Integer.valueOf(id)).getRecipes());
        model.addAttribute("nrOfRecipes",recipes.size());

        Page<Recipe> pages = recipeService.getAllRecipesByCategoryPage(Integer.valueOf(id), pageNumber, sortKey);
        model.addAttribute("recipes", pages);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("sortKey", sortKey);

        //TO DO: sa se elimine categoria curenta din lista
        List<Category> allCategories = categoryService.getAllCategories();
        model.addAttribute("allCategories", allCategories);

        return "category/show";
    }

    @RequestMapping(value = "/category/update/{id}", method = RequestMethod.GET)
    public String updateCategory(Model model,@PathVariable int id) {
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

        Category category = categoryService.findCategoryById(id);;
        model.addAttribute("category", category);

        return "/category/update";
    }

    //@PostMapping(value = "/category/update/{id}")
    @RequestMapping(value = "/category/update/{id}", method = RequestMethod.POST)
    public String updateCategory(@PathVariable("id") int id,@Valid Category category,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()){
            return "/category/update";
        }

        Category currentCategory = categoryService.findCategoryById(id);
        currentCategory.setName(category.getName());
        categoryService.updateCategory(currentCategory);

        allCategories(model);
        return "/category/index";
    }

    @RequestMapping(value = "/category/index", method = RequestMethod.GET)
    public String allCategories(Model model) {
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
        Collections.sort(categories, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });
        int nrPerColumn = categories.size() / 4;
        int surplus = categories.size() - (nrPerColumn * 4);
        int s = 0;
        int counter = 0;
        List<Category> list1 = null; List<Category> list2 = null; List<Category> list3 = null; List<Category> list4 = null;
        if (categories.size() > 0) {
            if (surplus > 0) {
                s = 1;
                surplus = surplus - 1;
            }
            list1 = categories.stream().limit(nrPerColumn + s).collect(Collectors.toList());
            counter = nrPerColumn + s;
            if (categories.size() > counter) {
                s = 0;
                if (surplus > 0) {
                    s = 1;
                    surplus = surplus - 1;
                }
                list2 = categories.stream().skip(counter).limit(nrPerColumn + s).collect(Collectors.toList()); //subCatgs.Skip(counter).Take(nrPerColumn + s).ToList();
                counter = counter + nrPerColumn + s;
                if (categories.size() > counter) {
                    s = 0;
                    if (surplus > 0) {
                        s = 1;
                        surplus = surplus - 1;
                    }
                    list3 = categories.stream().skip(counter).limit(nrPerColumn + s).collect(Collectors.toList()); //.Skip(counter).Take(nrPerColumn + s).ToList();
                    counter = counter + nrPerColumn + s;
                    if (categories.size() > counter) {
                        s = 0;
                        if (surplus > 0) {
                            s = 1;
                            surplus = surplus - 1;
                        }
                        list4 = categories.stream().skip(counter).limit(nrPerColumn + s).collect(Collectors.toList()); //Skip(counter).Take(nrPerColumn + s).ToList();
                    }

                }
            }
        }
        model.addAttribute("categoriesList1", list1);
        model.addAttribute("categoriesList2", list2);
        model.addAttribute("categoriesList3", list3);
        model.addAttribute("categoriesList4", list4);

        return "/category/index";
    }

    @RequestMapping("category/{id}/delete")
    public String deleteById(@PathVariable String id){
        categoryService.deleteById(Integer.valueOf(id));
        return "redirect:/category/index";
    }
}
