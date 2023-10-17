package com.chatApp.webSocketAPI.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/")
    public ResponseEntity<String> home(){
        return new ResponseEntity<String>("API Loads Fine", HttpStatus.OK);
    }
}
