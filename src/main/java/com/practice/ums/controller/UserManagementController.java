package com.practice.ums.controller;

import com.practice.ums.dto.RequestResponse;
import com.practice.ums.entity.OurUsers;
import com.practice.ums.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController

public class UserManagementController {
    @Autowired
    private UserManagementService userManagementService;
    @PostMapping("/auth/register")
    public ResponseEntity<RequestResponse> registerUser(@RequestBody RequestResponse registerUser) {
        return ResponseEntity.ok(userManagementService.register(registerUser));
    }
    @PostMapping("/auth/login")
    public ResponseEntity<RequestResponse> loginUser(@RequestBody RequestResponse loginUser) {
        return ResponseEntity.ok(userManagementService.login(loginUser));
    }
    @PostMapping("/auth/refresh")
    public ResponseEntity<RequestResponse> refreshToken(@RequestBody RequestResponse refreshToken) {
        return ResponseEntity.ok(userManagementService.refreshToken(refreshToken));
    }
    @GetMapping("/admin/get-all-users")
    public ResponseEntity<RequestResponse> getAllUsers(){
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }
    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<RequestResponse> getUserById(@PathVariable Integer userId){
        return ResponseEntity.ok(userManagementService.getUserById(userId));
    }
    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<RequestResponse> updateUserById(@PathVariable Integer userId, @RequestBody OurUsers req){
        return ResponseEntity.ok(userManagementService.updateUserById(userId,req));
    }
    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<RequestResponse> getMyProfile(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        RequestResponse response=userManagementService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<RequestResponse> deleteUserById(@PathVariable Integer userId){
        return ResponseEntity.ok(userManagementService.deleteUserById(userId));
    }

}
