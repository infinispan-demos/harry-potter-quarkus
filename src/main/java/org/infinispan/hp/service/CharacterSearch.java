package org.infinispan.hp.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.hp.model.HPCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.infinispan.client.Remote;

@ApplicationScoped
public class CharacterSearch {
   private static final Logger LOGGER = LoggerFactory.getLogger("CharacterSearch");

   @Inject
   @Remote(DataLoader.HP_CHARACTERS_NAME)
   RemoteCache<String, HPCharacter> characters;

   public HPCharacter getById(String id) {
      return characters.get(id);
   }

   public CompletionStage<HPCharacter> getByIdAsync(String id) {
      return characters.getAsync(id);
   }

   /**
    * Performs a simple full-text query on name and bio
    *
    * @param term
    * @return character names
    */
   public Set<String> search(String term) {
      if (characters == null) {
         LOGGER.error("Unable to search... Is He-Who-Must-Not-Be-Named around?");
         throw new IllegalStateException("Characters store is null. Try restarting the application");
      }
      String query = "FROM hp_monitoring.HPCharacter c"
      + " WHERE c.name LIKE '%"+ term + "%'"
      + " OR c.bio LIKE '%" + term + "%'";

      List<HPCharacter> result = characters.<HPCharacter>query(query).execute().list();
      return result.stream().map(HPCharacter::name).collect(Collectors.toSet());
   }

}
