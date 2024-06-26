package org.infinispan.hp;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import org.infinispan.hp.model.HPCharacter;
import org.infinispan.hp.service.CharacterSearch;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletionStage;

@Path("/harry-potter/character")
@Produces(MediaType.APPLICATION_JSON)
public class CharactersResource {

   @Inject
   CharacterSearch searchService;

   @GET
   @Path("/{id}")
   public HPCharacter byId(@PathParam("id") String id) {
      Log.info("Search by Id " + id);
      HPCharacter character = searchService.getById(id);
      if (character == null) {
         throw new WebApplicationException("Character with id of " + id + " does not exist.", 404);
      }
      return character;
   }

   @GET
   @Path("/async/{id}")
   public CompletionStage<HPCharacter> byIdAsync(@PathParam("id") String id) {
      Log.info("Search by Id Async " + id);
      return searchService.getByIdAsync(id).whenComplete((c, e) -> {
         if (e != null) {
            throw new WebApplicationException("Unexpected error", e, 500);
         }
         if (c == null) {
            throw new WebApplicationException("Character with id of " + id + " does not exist.", 404);
         }
      });
   }

   @GET
   @Path("/query")
   public Set<String> searchCharacter(@QueryParam("term") String term) {
      Log.info("Search by term " + term);
      if (term == null) {
         return Collections.emptySet();
      }
      return searchService.search(term);
   }
}
