package com.example.project.controller;

import com.example.project.model.Rating;
import com.example.project.model.Recipe;
import com.example.project.model.User;
import com.example.project.service.RatingService;
import com.example.project.service.RecipeService;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

@Controller
public class RatingController {

    @Autowired
    UserService userService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    RatingService ratingService;

    @RequestMapping(value = "rating/recipe/{recipeId}", method = RequestMethod.GET)
    public String showRecipeRatings(@PathVariable String recipeId, Model model){
        Recipe recipe = recipeService.findRecipeById(Integer.valueOf(recipeId));
        model.addAttribute("recipe", recipe);
        model.addAttribute("nrRatings",
                recipeService.findRecipeById(Integer.valueOf(recipeId)).getRatings().size());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        if(user != null){
            model.addAttribute("loggedUser", user);
            model.addAttribute("isAuth", "true");
            String role = user.getRoles().stream().findFirst().get().getRole().toUpperCase();
            model.addAttribute("role", role);

            Rating rating = new Rating();
            model.addAttribute("newRating", rating);
            boolean ratingAlreadyExists = ratingService.existsByUserAndRecipe(user, recipe);
            model.addAttribute("ratingAlreadyExists", String.valueOf(ratingAlreadyExists));
        }
        else{
            model.addAttribute("isAuth", "false");
        }

        return "rating/show";
    }

    @RequestMapping(value = "rating/recipe/{recipeId}", method = RequestMethod.POST)
    public String newRating(@Valid Rating newRating, BindingResult bindingResult,
                            @PathVariable String recipeId, Model model){
        if(bindingResult.hasErrors()){
            return "redirect:/rating/recipe/" + recipeId;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        newRating.setUser(user);
        Calendar cal = Calendar.getInstance();
        Date dateAdded = cal.getTime();
        newRating.setDate(dateAdded);
        Recipe recipe = recipeService.findRecipeById(Integer.valueOf(recipeId));
        newRating.setRecipe(recipe);

        Rating saveRating = ratingService.saveRating(newRating);

        Integer nrOfRatings = recipe.getRatings().size();
        Integer sumRatings = 0;
        for (Rating rt: recipe.getRatings()) {
            sumRatings += rt.getScore();
        }

        Double average = ((double)sumRatings / (double)nrOfRatings);
        Double newScore = Double.valueOf(new BigDecimal(average).setScale(2, RoundingMode.HALF_UP).doubleValue());

        recipe.setScore(newScore);
        recipeService.saveRecipe(recipe);

        return "redirect:/rating/recipe/" + recipeId;
    }

}
