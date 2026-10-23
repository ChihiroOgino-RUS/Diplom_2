package tests.user;

import client.StellarBurgersClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.UserCredentials;
import model.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.TestDataGenerator;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;

public class UserLoginTest {
    private StellarBurgersClient client;
    private UserModel user;
    private String accessToken;

    @Before
    public void setUp() {
        client = new StellarBurgersClient();
        user = TestDataGenerator.createUniqueUser();
        accessToken = client.createUser(user).then().extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }

    @DisplayName("Логин существующего пользователя")
    @Description("Успешный вход: код 200, success: true, accessToken начинается с Bearer")
    @Test
    public void loginExistingUserTest() {
        Response response = client.loginUser(new UserCredentials(user.getEmail(), user.getPassword()));

        assertEquals(SC_OK, response.statusCode());
        response.then()
                .body("success", equalTo(true))
                .body("accessToken", startsWith("Bearer "));
    }

    @DisplayName("Логин с неверным паролем")
    @Description("Неверный пароль: код 401 и сообщение email or password are incorrect")
    @Test
    public void loginWithWrongPasswordTest() {
        Response response = client.loginUser(new UserCredentials(user.getEmail(), "incorrectPassword"));

        assertEquals(SC_UNAUTHORIZED, response.statusCode());
        response.then()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @DisplayName("Логин с неверным логином")
    @Description("Неверный логин: код 401 и сообщение email or password are incorrect")
    @Test
    public void loginWithWrongLoginTest() {
        Response response = client.loginUser(new UserCredentials("incorrectEmail@yandex.ru", user.getPassword()));

        assertEquals(SC_UNAUTHORIZED, response.statusCode());
        response.then()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
