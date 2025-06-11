package net.sample.wordpress.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private String role;
   // private List<BlogDTO> blogs;
}
