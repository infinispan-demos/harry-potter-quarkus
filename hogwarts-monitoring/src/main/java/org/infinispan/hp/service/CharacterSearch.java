package org.infinispan.hp.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.hp.model.HPCharacter;
import org.infinispan.query.dsl.QueryBuilder;
import org.infinispan.query.dsl.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.infinispan.client.runtime.Remote;

@ApplicationScoped
public class CharacterSearch {
   private static final Logger LOGGER = LoggerFactory.getLogger("CharacterSearch");

   @Inject
   @Remote(DataLoader.HP_CHARACTERS_NAME)
   RemoteCache<Integer, HPCharacter> characters;

   public HPCharacter getById(Integer id) {
      return characters.get(id);
   }

   public CompletionStage<HPCharacter> getByIdAsync(Integer id) {
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
      QueryFactory queryFactory = Search.getQueryFactory(characters);

      QueryBuilder qb = queryFactory.from(HPCharacter.class)
            .having("name").like("%" + term + "%")
            .or()
            .having("bio").like("%" + term + "%");

      List<HPCharacter> characters = qb.build().list();
      return characters.stream().map(HPCharacter::getName).collect(Collectors.toSet());
   }

}
