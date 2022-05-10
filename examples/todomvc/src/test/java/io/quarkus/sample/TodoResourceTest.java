package io.quarkus.sample;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TodoResourceTest {

    @Test
    public void testAdd() {
        given()
                .body("{\"title\": \"Demo Todo\", \"order\": \"1\"}")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/api")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("title", equalTo("Demo Todo"));

        given()
                .body("{\"title\": \"Another Todo\", \"order\": \"1\"}")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/api")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .log().body()
                .body("title", equalTo("Another Todo"));
    }

    @Test
    public void testList() {
        given()
                .when().delete("/api/all")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        given()
                .body("{\"title\": \"Demo Todo\", \"order\": \"1\"}")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/api")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("title", equalTo("Demo Todo"));

        given()
                .body("{\"title\": \"Another Todo\", \"order\": \"1\"}")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/api")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .log().body()
                .body("title", equalTo("Another Todo"));

        given()
                .when().get("/api")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", is(2));
    }

    @Test
    public void testDeleteAll() {
        given()
                .when().delete("/api/all")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        given()
                .when().get("/api")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$.size()", is(0));
    }

    @Test
    public void testDeleteOne() {
        int todoId = given().log().all().contentType("application/json")
                .body("{\"title\": \"Delete Todo\", \"order\": \"1\"}")
                .when()
                .post("/api")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("id");

        given()
                .when().get("/api/{id}", todoId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(todoId));

        given()
                .when().delete("/api/{id}", todoId)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        given()
                .when().get("/api/{id}", todoId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);

    }


}
