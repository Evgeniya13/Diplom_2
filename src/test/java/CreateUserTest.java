import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class CreateUserTest {
    private final User user;
    private final int status;
    private final String message;
    private final static String USER_EMAIL = "Smaug" + Math.random() + "@example.com";
    private final static String PASSWORD = "Smaug1234";
    private final static String USER_NAME = "Smaug" + Math.random();
    private final static String URL = "/api/auth";
    private static String accessToken = "";

    public CreateUserTest(User user, int status, String message) {
        this.user = user;
        this.status = status;
        this.message = message;
    }

    @Parameterized.Parameters
    public static Object[][] userData() {
        return new Object[][]{
                {new User(USER_EMAIL, PASSWORD, USER_NAME), 200, null},
                {new User(USER_EMAIL, PASSWORD, USER_NAME), 403, "User already exists"},
                {new User("Master007@ya.ru", null, "Michail Bulgakov"), 403, "Email, password and name are required fields"},
                {new User(null, "12345", "Michail Bulgakov"), 403, "Email, password and name are required fields"},
                {new User("Master007@ya.ru", "12345", null), 403, "Email, password and name are required fields"},
        };
    }

    @Test
    @DisplayName("Create user")
    @Step("Compare message and status of response")
    public void userIsCreated() {
        ValidatableResponse response = Specifications.postRequest(user, URL + "/register");
        if (message != null) {
            response.assertThat().body("message", equalTo(message))
                    .and()
                    .statusCode(status);
        } else {
            response.assertThat()
                    .statusCode(status);
            if(status == 200) {
                accessToken = response.extract().path("accessToken").toString().split(" ")[1];
            }
        }
    }

    @AfterClass
    public static void deleteCourier() {
        if (!accessToken.isEmpty()) {
            RestAssured.given().auth().oauth2(accessToken)
                    .baseUri("https://stellarburgers.nomoreparties.site/api/auth/user")
                    .contentType(ContentType.JSON)
                    .when()
                    .delete().then().assertThat().statusCode(202);
        }
    }
}