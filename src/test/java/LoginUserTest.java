import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class LoginUserTest {
    private final static String USER_EMAIL = "Smaug" + Math.random() + "@example.com";
    private final static String PASSWORD = "Smaug1234";
    private final static String USER_NAME = "Smaug" + Math.random();
    private final static String URL = "/auth/login";
    private static String accessToken = "";
    private final Login login;
    private final int status;
    private final String message;

    public LoginUserTest(Login login, int status, String message) {
        this.login = login;
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
                {new Login(USER_EMAIL, PASSWORD), 200, null},
                {new Login("Master007@ya.ru", PASSWORD), 401, "email or password are incorrect"},
                {new Login(USER_EMAIL, "12345"), 401, "email or password are incorrect"},
                {new Login("Master007@ya.ru", "12345"), 401, "email or password are incorrect"},
                {new Login("Master007@ya.ru", null), 401, "email or password are incorrect"},
                {new Login(null, "12345"), 401, "email or password are incorrect"},
        };
    }

    @Test
    @DisplayName("Login user")
    @Step("Compare message and status of response")
    public void userLogin() {
        ValidatableResponse response = Specifications.postRequest(login, URL);
        if (message != null) {
            response.assertThat().body("message", equalTo(message))
                    .and()
                    .statusCode(status);
        } else {
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