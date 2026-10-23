package constants;

public class ApiConstants {
    public static final String BASE_URL = "https://qa-stellarburgers.education-services.ru";

    // Эндпоинты API
    public static final String USER_REGISTER_URL = BASE_URL + "/api/auth/register";
    public static final String USER_LOGIN_URL = BASE_URL + "/api/auth/login";
    public static final String ORDERS_URL = BASE_URL + "/api/orders";
    public static final String USER_DELETE_URL = BASE_URL + "/api/auth/user";
    public static final String INGREDIENTS_URL = BASE_URL + "/api/ingredients";

    private ApiConstants() {
    }
}
