import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class UpdateUserDataTest {
    private final static String USER_EMAIL = "smaug" + Math.random() + "@example.com";
    private final static String PASSWORD = "smaug1234";
    private final static String USER_NAME = "smaug" + Math.random();
    private final static String URL = "/auth/user";
    private static String accessToken = "";
    private final UserData userData;
    private final int status;
    private final String message;

    public UpdateUserDataTest(UserData userData, int status, String message) {
        this.userData = userData;
        this.status = status;
        this.message = message;

    }

    @BeforeClass
    @Step("Create user and get accessToken before test")
    public static void createTestUser() {
        accessToken = Specifications.createUser(USER_EMAIL, PASSWORD, USER_NAME);
    }

    @Parameterized.Parameters
    public static Object[][] userData() {
        return new Object[][]{
                {new UserData(USER_EMAIL, USER_NAME + "_updated"), 200, null},
                {new UserData(USER_EMAIL + "_updated", USER_NAME), 200, null},
                {new UserData(USER_EMAIL, USER_NAME), 401, "You should be authorised"},
        };
    }

    @Test
    @DisplayName("Update user data")
    @Step("Compare message and status of response")
    public void updateUserData() {
        ValidatableResponse response;
        if (message != null) {
            response = Specifications.patchRequest(userData, URL);
            response.assertThat().body("message", equalTo(message))
                    .and()
                    .statusCode(status);
        } else {
            response = Specifications.patchRequest(userData, URL, accessToken);
            response.assertThat()
                    .statusCode(status);
            JsonPath jsonPath = response.extract().jsonPath();
            UserData data = jsonPath.getObject("user", UserData.class);
            Assert.assertEquals(data.getEmail(), userData.getEmail());
            Assert.assertEquals(data.getName(), userData.getName());
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