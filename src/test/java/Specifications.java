import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

public class Specifications {

    final static String BASE_URL = "https://stellarburgers.nomoreparties.site/";

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

}
