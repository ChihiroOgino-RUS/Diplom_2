package utils;

import model.UserModel;

public class TestDataGenerator {
    public static UserModel createUniqueUser() {
        String uniquePart = String.valueOf(System.currentTimeMillis());
        String email = "testuser" + uniquePart + "@yandex.ru";
        String password = "password123";
        String firstName = "Natalia";

        return new UserModel(email, password, firstName);
    }
}
