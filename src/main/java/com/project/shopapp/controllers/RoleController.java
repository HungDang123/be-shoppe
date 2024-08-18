package com.project.shopapp.controllers;

import com.project.shopapp.models.Role;
import com.project.shopapp.services.IRoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("${api.prefix}/roles")
public class RoleController {
    IRoleService iRoleService;
    @GetMapping("")
    public ResponseEntity<?> getRoles(){
        try{
            List<Role> roles = iRoleService.roles();
            return ResponseEntity.ok(roles);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
