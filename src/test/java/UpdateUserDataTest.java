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
public class UpdateUserDataTest {
    private final UserData userData;
    private final int status;
    private final String message;
    private final static String USER_EMAIL = "Smaug" + Math.random() + "@example.com";
    private final static String PASSWORD = "Smaug1234";
    private final static String USER_NAME = "Smaug" + Math.random();
    private final static String URL = "/api/auth";
    private static String accessToken = "";

    public UpdateUserDataTest(UserData userData, int status, String message) {
        this.userData = userData;
        this.status = status;
        this.message = message;

    }

    @BeforeClass
    @Step("Create user before test")
    public static void createTestUser() {
        ValidatableResponse response =  Specifications.postRequest(new User(USER_EMAIL, PASSWORD, USER_NAME), URL + "/register");
        response.assertThat().statusCode(200);
        accessToken = response.extract().path("accessToken").toString().split(" ")[1];
    }

    @Parameterized.Parameters
    public static Object[][] userData() {
        return new Object[][]{
                {new UserData(USER_EMAIL, USER_NAME + "_Updated"), 200, null},
                {new UserData(USER_EMAIL, USER_NAME), 401, "You should be authorised"},
        };
    }

    @Test
    @DisplayName("Update user data")
    @Step("Compare message and status of response")
    public void updateUserData() {
        ValidatableResponse response;
        if (message != null) {
            response = Specifications.patchRequest(userData, URL + "/user");
            response.assertThat().body("message", equalTo(message))
                    .and()
                    .statusCode(status);
        } else {
            response = Specifications.patchRequest(userData, URL + "/user", accessToken);
            response.assertThat()
                    .statusCode(status);
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