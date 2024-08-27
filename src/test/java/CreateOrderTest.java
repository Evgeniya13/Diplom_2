import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;


@RunWith(Parameterized.class)
public class CreateOrderTest {
    private final static String USER_EMAIL = "Smaug" + Math.random() + "@example.com";
    private final static String PASSWORD = "Smaug1234";
    private final static String USER_NAME = "Smaug" + Math.random();
    private final static String URL = "/orders";
    private static String accessToken = "";
    private final static String[] INGREDIENTS_FOR_ORDER_WITH_WRONG_HASH = {"61c0c5a71d1f820bdaaa6d", "61c0c5aaa71", "61c0c5a71d1f8200"};
    private final static ArrayList<String> ingredientsWithWrongHash = new ArrayList<>(Arrays.asList(INGREDIENTS_FOR_ORDER_WITH_WRONG_HASH));
    private final Order order;
    private final int status;
    private final String message;
    private final boolean auth;

    public CreateOrderTest(Order order, int status, String message, boolean auth) {
        this.order = order;
        this.status = status;
        this.message = message;
        this.auth = auth;
    }

    @BeforeClass
    @Step("Create user and get accessToken before test")
    public static void createTestUser() {
        accessToken = Specifications.createUser(USER_EMAIL, PASSWORD, USER_NAME);
    }

    @Parameterized.Parameters
    public static Object[][] orderData() {
        return new Object[][]{
                {new Order(Specifications.getIngredients()), 200, null, true},
                {new Order(Specifications.getIngredients()), 200, null, false},
                {new Order(null), 400, "Ingredient ids must be provided", true},
                {new Order(ingredientsWithWrongHash), 500, null, true},
        };
    }

    @Test
    @DisplayName("Create order")
    @Step("Compare message and status of response")
    public void createOrder() {
        ValidatableResponse response;
        if (auth) {
            response = Specifications.postRequest(order,  URL, accessToken);
            if (message != null) {
                response.assertThat().body("message", equalTo(message))
                        .and()
                        .statusCode(status);
            } else {
                response.assertThat()
                        .statusCode(status);
            }
        } else {
            response = Specifications.postRequest(order, URL);
            response.assertThat()
                    .statusCode(status);
        }
    }

    @AfterClass
    @Step("Delete user after test")
    public static void deleteUser() {
        if (!accessToken.isEmpty()) {
            Specifications.deleteUser(accessToken);
        }
    }
}
