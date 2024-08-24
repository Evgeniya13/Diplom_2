import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.*;


@RunWith(Parameterized.class)
public class CreateOrderTest {
    private final Order order;
    private final int status;
    private final String message;
    private final static String USER_EMAIL = "Smaug" + Math.random() + "@example.com";
    private final static String PASSWORD = "Smaug1234";
    private final static String USER_NAME = "Smaug" + Math.random();
    private final static String URL = "/api";
    private static String accessToken = "";

    private final static String[] ingredientsForBurger = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa71", "61c0c5a71d1f82001bdaaa72"};

    public CreateOrderTest(Order order, int status, String message) {
        this.order = order;
        this.status = status;
        this.message = message;
    }

    @BeforeClass
    @Step("Create user before test")
    public static void createTestUser() {
        ValidatableResponse response =  Specifications.postRequest(new User(USER_EMAIL, PASSWORD, USER_NAME), URL + "/auth/register");
        response.assertThat().statusCode(200);
        accessToken = response.extract().path("accessToken").toString().split(" ")[1];
    }

    @Parameterized.Parameters
    public static Object[][] orderData() {
        return new Object[][]{
                {new Order(ingredientsForBurger), 200, null},
//                {new Login("Master007@ya.ru", PASSWORD), 401, "email or password are incorrect"},
//                {new Login(USER_EMAIL, "12345"), 401, "email or password are incorrect"},
//                {new Login("Master007@ya.ru", "12345"), 401, "email or password are incorrect"},
//                {new Login("Master007@ya.ru", null), 401, "email or password are incorrect"},
//                {new Login(null, "12345"), 401, "email or password are incorrect"},
        };
    }

    @Test
    @DisplayName("Create order")
    @Step("Compare message and status of response")
    public void userLogin() {
        ValidatableResponse response = Specifications.postRequest(order, URL + "/orders", accessToken);
        if (message != null) {
            response.assertThat().body("message", equalTo(message))
                    .and()
                    .statusCode(status);
        } else {
            response.assertThat()
                    .statusCode(status);
            System.out.println(response.extract().body().toString());
        }
    }

    @AfterClass
    public static void deleteUser() {
        if (!accessToken.isEmpty()) {
            RestAssured.given().auth().oauth2(accessToken)
                    .baseUri("https://stellarburgers.nomoreparties.site/api/auth/user")
                    .contentType(ContentType.JSON)
                    .when()
                    .delete().then().assertThat().statusCode(202);

        }
    }
}
