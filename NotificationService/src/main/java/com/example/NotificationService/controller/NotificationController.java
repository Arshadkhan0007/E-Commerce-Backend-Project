package com.example.NotificationService.controller;

import com.example.NotificationService.dto.SimpleEmailDetails;
import com.example.NotificationService.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

//    @PostMapping("/")
//    public ResponseEntity<Void> sendMessage(@RequestBody SimpleEmailDetails simpleEmailDetails) {
//        service.sendSimpleMail(simpleEmailDetails);
//        return ResponseEntity.ok().build();
//    }



}
