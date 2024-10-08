package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    User updateUser(UserDTO userDTO,Long id) throws Exception;
    String login(String phoneNumber,String password,Long roleId) throws Exception;
    User getUserDetailFromToken(String token) throws Exception;
}
