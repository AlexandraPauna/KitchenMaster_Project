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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;

    //TO DO
    //@Autowired
    //private RatingService ratingService;

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

            List<Recipe> recipes = recipeService.getAllRecipesForLoggedUser(user);
            model.addAttribute("recipes", recipes);
            model.addAttribute("nrOfRecipes", recipes.size());

            //TO DO
//            List<Rating> ratings = ratingService.getAllRatingsForLoggedUser(user);
//            model.addAttribute("nrOfRatings", ratings.size());

            return "/profile";
    }

    @RequestMapping(value = "/user/update/{id}", method = RequestMethod.GET)
    public String updateUser(Model model, @PathVariable int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        if(user != null){
            if(user.getId() != Integer.valueOf(id)){
                return "redirect:/home/index";
            }
            else{
                model.addAttribute("loggedUser", user);
                model.addAttribute("user",user);
                model.addAttribute("isAuth", "true");
                String role = user.getRoles().stream().findFirst().get().getRole().toUpperCase();
                model.addAttribute("role", role);

                List<Recipe> recipes = recipeService.getAllRecipesForLoggedUser(user);
                model.addAttribute("recipes", recipes);
                model.addAttribute("nrOfRecipes", recipes.size());

                //TO DO
//                List<Rating> ratings = ratingService.getAllRatingsForLoggedUser(user);
//                model.addAttribute("nrOfRatings", ratings.size());

                return "/user/update";
            }
        }
        else{
            model.addAttribute("isAuth", "false");
            return "/home/index";
        }

    }


    @PostMapping(value = "/user/update/{id}")
    public String updateUser(@PathVariable("id") int id, @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user2 = userService.findUserByUserName(auth.getName());
            model.addAttribute("loggedUser", user2);

            return "/user/update";
        }
        else
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUserName(auth.getName());
            currentUser.setUserName (user.getUserName());
            currentUser.setFirstName(user.getFirstName());
            currentUser.setLastName(user.getLastName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPassword(currentUser.getPassword());
            userService.updateUser(currentUser);
            if(currentUser != null){
                model.addAttribute("loggedUser", currentUser);
                model.addAttribute("isAuth", "true");
                String role = currentUser.getRoles().stream().findFirst().get().getRole().toUpperCase();
                model.addAttribute("role", role);

                List<Recipe> recipes = recipeService.getAllRecipesForLoggedUser(currentUser);
                model.addAttribute("recipes", recipes);
                model.addAttribute("nrOfRecipes", recipes.size());

                //TO DO
//                List<Rating> ratings = ratingService.getAllRatingsForLoggedUser(currentUser);
//                model.addAttribute("nrOfRatings", ratings.size());
            }
            else{
                model.addAttribute("isAuth", "false");
                //return "/home/index";
            }
            return "/profile";
        }

    }
}
