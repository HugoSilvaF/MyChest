package com.github.hugosilvaf2.mychest.controller;

import com.github.hugosilvaf2.mychest.service.UserService;

public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService(){
        return userService;
    }
    
}
