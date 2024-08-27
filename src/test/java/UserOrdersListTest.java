import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class UserOrdersListTest {
    private final static String USER_EMAIL = "Smaug" + Math.random() + "@example.com";
    private final static String PASSWORD = "Smaug1234";
    private final static String USER_NAME = "Smaug" + Math.random();
    private final static String URL = "/orders";
    private static ArrayList<String> ingredientsForOrder;
    private static String accessToken = "";
    private final int status;
    private final String message;
    private final boolean auth;

    public UserOrdersListTest(int status, String message, boolean auth) {
        this.status = status;
        this.message = message;
        this.auth = auth;
    }

    @BeforeClass
    @Step("Create user and get ingredients before test")
    public static void createTestUser() {
        ingredientsForOrder = Specifications.getIngredients();
        accessToken = Specifications.createUser(USER_EMAIL, PASSWORD, USER_NAME);
    }

    @BeforeClass
    @Step("Create order before test")
    public static void createOrder() {
        Specifications.postRequest(new Order(ingredientsForOrder), URL, accessToken).assertThat().statusCode(200);
    }

    @Parameterized.Parameters
    public static Object[][] orderData() {
        return new Object[][]{
                {200, null, true},
                {401, "You should be authorised", false},
        };
    }

    @Test
    @DisplayName("Get user orders list")
    @Step("Compare message and status of response")
    public void getUserOrdersList() {
        ValidatableResponse response;
        if (auth) {
            response = Specifications.getRequest(URL, accessToken);
            response.assertThat()
                    .statusCode(status);
            Assert.assertEquals(response.extract().jsonPath().getList("orders").size(), 1);
        } else {
            response = Specifications.getRequest(URL);
            response.assertThat().body("message", equalTo(message))
                    .and()
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
