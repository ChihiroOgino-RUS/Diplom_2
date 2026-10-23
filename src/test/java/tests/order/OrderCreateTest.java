package tests.order;

import client.StellarBurgersClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.OrderRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.TestDataGenerator;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class OrderCreateTest {
    private StellarBurgersClient client;
    private String accessToken;
    private String firstIngredient;
    private String secondIngredient;

    @Before
    public void setUp() {
        client = new StellarBurgersClient();
        accessToken = client.createUser(TestDataGenerator.createUniqueUser())
                .then().extract().path("accessToken");
        Response ingredients = client.getIngredients();
        firstIngredient = ingredients.then().extract().path("data[0]._id");
        secondIngredient = ingredients.then().extract().path("data[1]._id");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
        }
    }

    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Успешное создание: код 200, success: true, в ответе номер заказа")
    @Test
    public void createOrderWithAuthAndIngredientsTest() {
        Response response = client.createOrder(accessToken, new OrderRequest(List.of(firstIngredient, secondIngredient)));

        assertEquals(200, response.statusCode());
        response.then()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    // По заданию ожидаем 401. На QA-бэкенде фактически 200 (анонимный заказ создаётся)
    @DisplayName("Создание заказа без авторизации")
    @Description("Без токена: код 401 и сообщение You should be authorised")
    @Test
    public void createOrderWithoutAuthTest() {
        Response response = client.createOrder(null, new OrderRequest(List.of(firstIngredient, secondIngredient)));

        assertEquals(401, response.statusCode());
        response.then()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @DisplayName("Создание заказа без ингредиентов")
    @Description("Пустой список ингредиентов: код 400 и сообщение Ingredient ids must be provided")
    @Test
    public void createOrderWithoutIngredientsTest() {
        Response response = client.createOrder(accessToken, new OrderRequest(List.of()));

        assertEquals(400, response.statusCode());
        response.then()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @DisplayName("Создание заказа с невалидным хешем ингредиента")
    @Description("Несуществующий хеш: код 400")
    @Test
    public void createOrderWithInvalidIngredientHashTest() {
        Response response = client.createOrder(accessToken, new OrderRequest(List.of("000000000000000000000000", "111111111111111111111111")));

        assertEquals(400, response.statusCode());
        response.then()
                .body("success", equalTo(false))
                .body("message", equalTo("One or more ids provided are incorrect"));
    }
}
