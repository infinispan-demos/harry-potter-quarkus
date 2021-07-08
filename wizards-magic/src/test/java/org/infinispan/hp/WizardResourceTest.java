package org.infinispan.hp;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(InfinispanResource.class)
public class WizardResourceTest {

   @Test
   public void testHeath() {
      given()
            .when().get("/harry-potter/magic")
            .then()
            .statusCode(200)
            .body(is("Wizard Resource is ready for magic!"));
   }

   @Test
   public void testAdd() {
      given()
            .body("{\"caster\": \"Harry Potter\", \"curse\": \"Expelliarmus\", \"type\": \"STUDENT\"}")
            .header("Content-Type", MediaType.APPLICATION_JSON)
            .when()
            .post("/harry-potter/magic")
            .then()
            .statusCode(201);
   }
}
