package org.infinispan.hp;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.infinispan.hp.model.HPCharacter;
import org.infinispan.hp.service.CharacterSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/harry-potter/character")
@Produces(MediaType.APPLICATION_JSON)
public class CharactersResource {
   private static final Logger LOGGER = LoggerFactory.getLogger(CharactersResource.class.getName());

   @Inject
   CharacterSearch searchService;

   @GET
   @Path("/{id}")
   public HPCharacter byId(@PathParam("id") Integer id) {
      LOGGER.info("Search by Id " + id);
      HPCharacter character = searchService.getById(id);
      if (character == null) {
         throw new WebApplicationException("Character with id of " + id + " does not exist.", 404);
      }
      return character;
   }

   @GET
   @Path("/async/{id}")
   public CompletionStage<HPCharacter> byIdAsync(@PathParam("id") Integer id) {
      LOGGER.info("Search by Id Async " + id);
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
      LOGGER.info("Search by term " + term);
      if (term == null) {
         return Collections.emptySet();
      }
      return searchService.search(term);
   }
}
