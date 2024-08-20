package com.project.shopapp.controllers;

import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.UserResponse;
import com.project.shopapp.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.project.shopapp.dtos.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class UserController {
    private final IUserService iUserService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    @PostMapping("/register")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result,
            HttpServletRequest request
            ) {
        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                return ResponseEntity.badRequest().body("Password does not match");
            }
//             Locale locale = localeResolver.resolveLocale(request);
            return ResponseEntity.ok(new ResponseObject("OK","Register is successful",iUserService.createUser(userDTO)));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
            ) {
        // Kiểm tra thông tin đăng nhập và sinh token
        // Trả về token trong response
        try {
            String token = iUserService.login(
                    userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId());
//            Locale locale = localeResolver.resolveLocale(request);
//            System.out.println(locale);
            return ResponseEntity.ok(new ResponseObject("OK","Login is successfully",token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/detail")
    public ResponseEntity<?> getDetailUser(@RequestHeader("Authorization") String token){
        try {
            String extractToken = token.substring(7); // loaij bor Bearer ....
            User user = iUserService.getUserDetailFromToken(extractToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());        }
    }
    @PutMapping("/details/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") Long id,
                                                   @RequestBody UserDTO userDTO,
                                                   @RequestHeader("Authorization") String authorizationHeader){
        try {
            String extractToken = authorizationHeader.substring(7);
            User user = iUserService.getUserDetailFromToken(extractToken);
            if(user.getId() != id){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updateUser = iUserService.updateUser(userDTO,id);
            return ResponseEntity.ok(UserResponse.fromUser(updateUser));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
