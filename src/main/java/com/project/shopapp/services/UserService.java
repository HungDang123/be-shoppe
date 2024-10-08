package com.project.shopapp.services;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.PermitDenyException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception{
        String phoneNumber = userDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
             throw new DataIntegrityViolationException("Phone number already exist");
        }
        Role role =roleRepository.findById(userDTO.getRoleId()).orElseThrow(
                ()->new DataNotFoundException("Role id is not exist"));
        if(role.getName().equals(Role.ADMIN)){
            throw new PermitDenyException("you cannot register account with role is admin");
        }
            User user = User.builder()
                    .fullName(userDTO.getFullName())
                    .address(userDTO.getAddress())
                    .password(userDTO.getPassword())
                    .dateOfBirth(userDTO.getDateOfBirth())
                    .phoneNumber(userDTO.getPhoneNumber())
                    .facebookAccountId(userDTO.getFacebookAccountId())
                    .googleAccountId(userDTO.getGoogleAccountId())
                    .build();

            user.setRole(role);
            if(userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0){
                String password= userDTO.getPassword();
                String encodePassword = passwordEncoder.encode(password);
                user.setPassword(encodePassword);
            }
            userRepository.save(user);
            return user;
    }

    @Override
    public User updateUser(UserDTO userDTO, Long id) throws Exception {
       User userExist = userRepository.findById(id).orElseThrow(
               ()-> new DataNotFoundException("User is not exist")
       );
       String newNumberPhone = (userDTO.getPhoneNumber());
       if(!userExist.getPhoneNumber().equals(newNumberPhone) && userRepository.existsByPhoneNumber(newNumberPhone)){
           throw new DataNotFoundException("Phone number already exist");
       }
       Role updateRole = roleRepository.findById(userDTO.getRoleId()).
               orElseThrow(
                       ()-> new DataNotFoundException("Role is not exist"));
       if(userExist.getRole().getName().equals(Role.ADMIN)){
           throw new PermitDenyException("You cannot update to an admin account");
       }
       if(userDTO.getFullName() !=null){
           userExist.setFullName(userDTO.getFullName());
       }
        if(userDTO.getAddress() !=null){
            userExist.setAddress(userDTO.getAddress());
        }
        if(userDTO.getDateOfBirth() !=null){
            userExist.setDateOfBirth(userDTO.getDateOfBirth());
        } if(newNumberPhone !=null){
            userExist.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if(userDTO.getFacebookAccountId() > 0){
            userExist.setFacebookAccountId(userDTO.getFacebookAccountId());
        } if(userDTO.getGoogleAccountId() > 0){
            userExist.setGoogleAccountId(userDTO.getGoogleAccountId());
        }
        return userRepository.save(userExist);
    }

    @Override
    public String login(String phoneNumber, String password,Long roleId) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Invalid phoneNumber / password");
        }
        User user = optionalUser.get();
        if(user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0){
            if(!passwordEncoder.matches(password,user.getPassword())){
                throw new BadCredentialsException("Wrong phone number or password");
            }
        }
        Role role = roleRepository.findById(roleId).get();
        if(!roleId.equals(user.getRole().getId())){
            throw new DataNotFoundException("Role is not exist");
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber,password,user.getAuthorities()
        );
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        return jwtTokenUtils.generateToken(user);
    }

    @Override
    public User getUserDetailFromToken(String token) throws Exception {
            if(jwtTokenUtils.isTokenExpiration(token)){
                throw new Exception("Token is expiration");
            }
            String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
            Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
            if(userOptional.isPresent()){
                return userOptional.get();
            }else{
                throw new Exception("User not found");
            }

    }
}
