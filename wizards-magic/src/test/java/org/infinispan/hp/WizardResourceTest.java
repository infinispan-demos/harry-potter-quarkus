package org.infinispan.hp;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import javax.ws.rs.core.MediaType;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Ignore
public class WizardResourceTest {

   @Test
   public void testHeath() {
      given()
            .when().get("/harry-potter/wizard")
            .then()
            .statusCode(200)
            .body(is("Wizard Resource is ready for magic!"));
   }

   @Test
   public void testAdd() {
      given()
            .body("{\"caster\": \"Harry Potter\", \"spell\": \"Expelliarmus\", \"type\": \"STUDENT\"}")
            .header("Content-Type", MediaType.APPLICATION_JSON)
            .when()
            .post("/harry-potter/wizard")
            .then()
            .statusCode(201);
   }
}
