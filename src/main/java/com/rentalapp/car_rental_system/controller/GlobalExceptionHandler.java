package com.rentalapp.car_rental_system.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ModelAndView handleDatabaseError(DataAccessException ex) {
        ModelAndView mav = new ModelAndView("error-db");
        mav.addObject("message", "We're currently experiencing database issues. Please try again later.");
        return mav;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ModelAndView handleAuthFailure(UsernameNotFoundException ex) {
        if (ex.getCause() instanceof DataAccessException) {
            ModelAndView mav = new ModelAndView("error-db");
            mav.addObject("message", "Login failed: the database is currently unavailable.");
            return mav;
        }
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("error", "Invalid username or password.");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericError(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "Something went wrong. Please try again.");
        return mav;
    }
}
