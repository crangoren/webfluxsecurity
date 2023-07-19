package srk.webfluxsecurity.entity.mapper;

import org.mapstruct.Mapper;
import srk.webfluxsecurity.dto.UserDto;
import srk.webfluxsecurity.entity.UserEntity;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto map(UserEntity userEntity);
    UserEntity map(UserDto userDto);
}
