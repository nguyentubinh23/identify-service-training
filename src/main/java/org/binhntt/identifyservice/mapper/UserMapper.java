package org.binhntt.identifyservice.mapper;

import org.binhntt.identifyservice.dto.request.UserCreationRequest;
import org.binhntt.identifyservice.dto.request.UserUpdateRequest;
import org.binhntt.identifyservice.dto.response.UserResponse;
import org.binhntt.identifyservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    @Mapping(source = "firstName", target = "lastName")
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
