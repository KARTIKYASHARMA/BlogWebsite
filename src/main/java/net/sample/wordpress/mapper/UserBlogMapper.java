package net.sample.wordpress.mapper;


import net.sample.wordpress.dto.BlogDTO;
import net.sample.wordpress.dto.UserDTO;
import net.sample.wordpress.entity.Blog;
import net.sample.wordpress.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface UserBlogMapper {

    UserDTO userToUserDTO(User user);

    BlogDTO blogToBlogDTO(Blog blog);

    List<BlogDTO> blogsToBlogDtos(List<Blog> blogs);

    User userDTOToUser(UserDTO userDTO);

    Blog blogDTOToBlog(BlogDTO blogDTO);
    @Mapping(target = "user", ignore = true) // You will set user manually when converting BlogDto to Blog
    Blog blogDtoToBlog(BlogDTO dto);

}
