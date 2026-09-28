package com.eve.eve.dto;



import com.eve.eve.entity.User;

public record UserResponse(
        Long id,
        String name,
        String email
) {

    public static UserResponse from(
            User user
    ) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
