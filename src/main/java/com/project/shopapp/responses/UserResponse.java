package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    Boolean active;
    @JsonProperty("date_of_birth")
    Date dateOfBirth;
    @JsonProperty("facebook_account_id")
    int facebookAccountId;
    @JsonProperty("google_account_id")
    int googleAccountId;
    String phone_number,fullname,address;
    Role role;
    public static UserResponse fromUser(User user){
        return UserResponse.builder()
                .id(user.getId())
                .active(user.getIsActive())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .fullname(user.getFullName())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .role(user.getRole())
                .phone_number(user.getPhoneNumber())
                .build();
    }
}
