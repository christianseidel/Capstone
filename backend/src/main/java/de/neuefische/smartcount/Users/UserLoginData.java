package de.neuefische.smartcount.Users;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginData {

    private String username;
    private String password;

}
