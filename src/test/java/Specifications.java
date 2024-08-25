import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

public class Specifications {

    final static String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    public static ValidatableResponse getRequest(String url) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get(url).then();
    }

    public static ValidatableResponse getRequest(String url, String token) {
        return RestAssured.given().auth().oauth2(token)
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .when()
                .get(url).then();
    }

    public static ValidatableResponse postRequest(Object object, String url) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(object)
                .when()
                .post(url).then();
    }

    public static ValidatableResponse postRequest(Object object, String url, String token) {
        return RestAssured.given().auth().oauth2(token)
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(object)
                .when()
                .post(url).then();
    }

    public static ValidatableResponse patchRequest(Object object, String url) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(object)
                .when()
                .patch(url).then();
    }

    public static ValidatableResponse patchRequest(Object object, String url, String token) {
        return RestAssured.given().auth().oauth2(token)
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(object)
                .when()
                .patch(url).then();
    }

    public static String createUser(String email, String password, String name) {
        ValidatableResponse response = Specifications.postRequest(new User(email, password, name), BASE_URL + "/auth/register");
        response.assertThat().statusCode(200);
        return response.extract().path("accessToken").toString().split(" ")[1];
    }

    public static void deleteUser(String token) {
        if (!token.isEmpty()) {
            RestAssured.given().auth().oauth2(token)
                    .baseUri(BASE_URL + "/auth/user")
                    .contentType(ContentType.JSON)
                    .when()
                    .delete().then().assertThat().statusCode(202);
        }
    }

}
