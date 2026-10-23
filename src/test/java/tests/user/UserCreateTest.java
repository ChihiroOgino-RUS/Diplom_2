package tests.user;

import client.StellarBurgersClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.TestDataGenerator;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;

public class UserCreateTest {
    private StellarBurgersClient client;
    private String accessToken;

    @Before
    public void setUp() {
        client = new StellarBurgersClient();
        accessToken = null;
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }

    @DisplayName("Создание уникального пользователя")
    @Description("Успешная регистрация: код 200, success: true, в ответе токены и данные пользователя")
    @Test
    public void createUniqueUserTest() {
        UserModel user = TestDataGenerator.createUniqueUser();

        Response response = client.createUser(user);
        accessToken = response.then().extract().path("accessToken");

        assertEquals(SC_OK, response.statusCode());
        response.then()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", notNullValue());
    }

    @DisplayName("Создание уже зарегистрированного пользователя")
    @Description("Дубликат email: код 403 и сообщение User already exists")
    @Test
    public void createDuplicateUserTest() {
        UserModel user = TestDataGenerator.createUniqueUser();
        accessToken = client.createUser(user).then().extract().path("accessToken");

        Response duplicate = client.createUser(user);

        assertEquals(SC_FORBIDDEN, duplicate.statusCode());
        duplicate.then()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @DisplayName("Создание пользователя без обязательного поля name")
    @Description("Без поля name: код 403 и сообщение об обязательных полях")
    @Test
    public void createUserWithoutNameTest() {
        UserModel user = TestDataGenerator.createUniqueUser();
        user.setName(null);

        Response response = client.createUser(user);

        assertEquals(SC_FORBIDDEN, response.statusCode());
        response.then()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @DisplayName("Создание пользователя без email")
    @Description("Без поля email: код 403 и сообщение об обязательных полях")
    @Test
    public void createUserWithoutEmailTest() {
        UserModel user = TestDataGenerator.createUniqueUser();
        user.setEmail(null);

        Response response = client.createUser(user);

        assertEquals(SC_FORBIDDEN, response.statusCode());
        response.then()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @DisplayName("Создание пользователя без пароля")
    @Description("Без поля password: код 403 и сообщение об обязательных полях")
    @Test
    public void createUserWithoutPasswordTest() {
        UserModel user = TestDataGenerator.createUniqueUser();
        user.setPassword(null);

        Response response = client.createUser(user);

        assertEquals(SC_FORBIDDEN, response.statusCode());
        response.then()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
