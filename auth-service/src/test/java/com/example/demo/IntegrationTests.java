package com.example.demo;

import com.example.demo.web.AuthenticationRequest;
import com.example.demo.web.UserForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Slf4j
public class IntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    ObjectMapper objectMapper;

    private String user_token;

    private String admin_token;

    @Before
    public void setup() {
        RestAssured.port = this.port;
        user_token = given()
            .contentType(ContentType.JSON)
            .body(AuthenticationRequest.builder().username("user").password("password").build())
            .when().post("/auth/signin")
            .andReturn().jsonPath().getString("token");
        log.debug("Got user_token:" + user_token);

        admin_token = given()
                .contentType(ContentType.JSON)
                .body(AuthenticationRequest.builder().username("admin").password("password").build())
                .when().post("/auth/signin")
                .andReturn().jsonPath().getString("token");
        log.debug("Got admin_token:" + admin_token);
    }

    @Test
    public void getAllUsersUnAuthenticated() throws Exception {
        //@formatter:off
         given()

            .accept(ContentType.JSON)

        .when()
            .get("/users")

        .then()
            .assertThat()
            .statusCode(403);
         //@formatter:on
    }

    @Test
    public void getAllUsersAuthenticatedAsUser() throws Exception {
        //@formatter:off
        given()

            .header("Authorization", "Bearer "+user_token)
            .accept(ContentType.JSON)

        .when()

            .get("/users")

        .then()

            .assertThat()
            .content(not(containsString("admin")))
            .statusCode(HttpStatus.SC_OK);

        //@formatter:on
    }

    @Test
    public void getAllUsersAuthenticatedAsAdmin() throws Exception {
        //@formatter:off
        given()

            .header("Authorization", "Bearer "+admin_token)
            .accept(ContentType.JSON)

        .when()

            .get("/users")

        .then()

            .assertThat()
            .content(containsString("admin"))
            .content(containsString("\"username\" : \"user\""))
            .statusCode(HttpStatus.SC_OK);

        //@formatter:on
    }

    @Test
    public void testSave() throws Exception {
        //@formatter:off
        given()

            .contentType(ContentType.JSON)
            .body(UserForm.builder().username("test").build())

        .when()
            .post("/users")

        .then()
            .statusCode(403);

        //@formatter:on
    }

    @Test
    public void testSaveWithUserAuth() throws Exception {

        //@formatter:off
        given()
            .header("Authorization", "Bearer "+user_token)
            .contentType(ContentType.JSON)
            .body(UserForm.builder().username("test").build())

        .when()
            .post("/users")

        .then()
            .statusCode(403);

        //@formatter:on
    }

    @Test
    public void testSaveWithAdminAuth() throws Exception {

        //@formatter:off
        given()
            .header("Authorization", "Bearer "+admin_token)
            .contentType(ContentType.JSON)
            .body(UserForm.builder().username("test").build())

        .when()
            .post("/users")

        .then()
            .statusCode(201);

        //@formatter:on
    }


    @Test
    public void testSaveWithAdminAuthInvalidPacket() throws Exception {

        //@formatter:off
        given()
            .header("Authorization", "Bearer "+admin_token)
            .contentType(ContentType.JSON)
            .body("{ }")

        .when()
            .post("/users")

        .then()
            .content(is("{\n" +
                    "  \"username\" : \"must not be null\"\n" +
                    "}"))
            .statusCode(400);

        //@formatter:on
    }
}
