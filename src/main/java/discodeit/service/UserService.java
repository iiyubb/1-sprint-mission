package discodeit.service;

import discodeit.dto.binarycontent.AddBinaryContentRequest;
import discodeit.dto.user.CreateUserRequest;
import discodeit.dto.user.UpdateUserRequest;
import discodeit.dto.user.UserDto;
import discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(CreateUserRequest createUserRequest, Optional<AddBinaryContentRequest> addBinaryContentRequest);
    UserDto find(UUID userId);
    List<UserDto> findAll();
    User update(UUID userId, UpdateUserRequest updateUserRequest, Optional<AddBinaryContentRequest> addBinaryContentRequest);
    void delete(UUID userId);
}
