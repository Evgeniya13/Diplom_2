import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class UserTest {
    private final User user;
    private final int status;
    private final String message;
    private final static String USER_EMAIL = "Smaug" + Math.random() + "@example.com";
    private final static String PASSWORD = "Smaug1234";
    private final static String USER_NAME = "Smaug" + Math.random();
    private static int userId = 0;

    public UserTest(User user, int status, String message) {
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
        if (message != null) {
            Specifications.postRequest(user, "/api/auth/register")
                    .assertThat().body("message", equalTo(message))
                    .and()
                    .statusCode(status);
        } else {
            Specifications.postRequest(user, "/api/auth/register")
                    .assertThat()
                    .statusCode(status);
            if(status == 200) {
                userId =
            }
        }
    }
}