package com.example.project.controller;

import com.example.project.model.User;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value="/registration")
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @PostMapping(value = "/registration")
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult){

        ModelAndView modelAndView= new ModelAndView();
        User userExists = userService.findUserByUserName(user.getUserName());
        if(userExists != null){
            bindingResult.rejectValue("userName", "error.user",
                    "There is already a user registered with the user name provided");
        }
        if(bindingResult.hasErrors()){
            modelAndView.setViewName("registration");
        }
        else{
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");
        }

        return modelAndView;
    }

    @GetMapping(value="/login")
    public ModelAndView login(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @PostMapping(value="/login")
    public ModelAndView loggedUser(){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
            model.addAttribute("loggedUser", user);
            model.addAttribute("isAuth", "true");
            String role = user.getRoles().stream().findFirst().get().getRole().toUpperCase();
            model.addAttribute("role", role);

            //TO DO
//            List<Recipe> recipes = recipeService.getAllRecipesForLoggedUser(user);
//            model.addAttribute("recipes", recipes);
//            model.addAttribute("nrOfRecipes", recipes.size());
//
//            List<Rating> ratings = ratingService.getAllRatingsForLoggedUser(user);
//            model.addAttribute("nrOfRatings", ratings.size());

            return "/profile";
    }
}
