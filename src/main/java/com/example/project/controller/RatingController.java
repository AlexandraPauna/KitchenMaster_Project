package com.example.project.controller;

import com.example.project.service.RatingService;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RatingController {

    @Autowired
    UserService userService;

    @Autowired
    RatingService ratingService;

}
