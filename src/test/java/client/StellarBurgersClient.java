package client;

import constants.ApiConstants;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.OrderRequest;
import model.UserCredentials;
import model.UserModel;

import static io.restassured.RestAssured.given;

public class StellarBurgersClient {

    @Step("Создание пользователя")
    public Response createUser(UserModel user) {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post(ApiConstants.USER_REGISTER_URL);
    }

    @Step("Логин пользователя")
    public Response loginUser(UserCredentials credentials) {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(credentials)
                .when()
                .post(ApiConstants.USER_LOGIN_URL);
    }

    @Step("Создание заказа")
    public Response createOrder(String token, OrderRequest order) {
        RequestSpecification request = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(order);
        if (token != null) {
            request.header("Authorization", token);
        }
        return request.when()
                .post(ApiConstants.ORDERS_URL);
    }

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return given()
                .log().all()
                .when()
                .get(ApiConstants.INGREDIENTS_URL);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return given()
                .log().all()
                .header("Authorization", token)
                .when()
                .delete(ApiConstants.USER_DELETE_URL);
    }
}
