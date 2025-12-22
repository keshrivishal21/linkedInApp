package com.vishal.linkedInProject.usersService.utils;

import static org.mindrot.jbcrypt.BCrypt.*;

public class BCrypt {

    public static String hash(String s){
        return hashpw(s,gensalt());
    }

    public static boolean match(String passwordText, String hashedPassword){
        return checkpw(passwordText, hashedPassword);
    }
}
