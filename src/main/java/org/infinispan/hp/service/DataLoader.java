package org.infinispan.hp.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.hp.model.HPCharacter;
import org.infinispan.hp.model.HPMagic;
import org.infinispan.hp.model.HPSpell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

/**
 * Service to cleanup and load application data
 */
@ApplicationScoped
public class DataLoader {
   private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class.getName());

   public static final String HP_CHARACTERS_NAME = "characters";
   public static final String HP_SPELLS_NAME = "spells";
   public static final String HP_MAGIC_NAME = "magic";

   @Inject
   @ConfigProperty(name = "characters.filename")
   String charactersFileName;

   @Inject
   @ConfigProperty(name = "spells.filename")
   String spellsFileName;

   @Inject
   RemoteCacheManager cacheManager;

   /**
    * Listens startup event to load the data
    */
   void onStart(@Observes @Priority(value = 1) StartupEvent ev) {
      LOGGER.info("On start - clean and load");
      // Get or create caches
      RemoteCache<Integer, HPCharacter> characters = cacheManager.administration().getOrCreateCache(HP_CHARACTERS_NAME, "default");
      RemoteCache<Integer, HPSpell> spells = cacheManager.administration().getOrCreateCache(HP_SPELLS_NAME, "default");
      RemoteCache<String, HPMagic> magic = cacheManager.administration().getOrCreateCache(HP_MAGIC_NAME, "default");

      LOGGER.info("Existing stores are " + cacheManager.getCacheNames().toString());

      // Cleanup data
      cleanupCaches(characters, spells, magic);

      // Load Ref data
      loadData(characters, spells);
   }

   private void loadData(RemoteCache<Integer, HPCharacter> characters, RemoteCache<Integer, HPSpell> spells) {
      try {
         loadCharacters(characters);
         LOGGER.info("Characters loaded. Size: " + characters.size());
      } catch (Exception e) {
         LOGGER.error("Unable to load characters on startup. Are you a Muggle or a Wizard?", e);
      }

      try {
         loadSpells(spells);
         LOGGER.info("Spells loaded. Size: " + spells.size());
      } catch (Exception e) {
         LOGGER.error("Unable to load spells data on startup. Are you a Muggle or a Wizard?", e);
      }
   }

   private void cleanupCaches(RemoteCache<Integer, HPCharacter> characters, RemoteCache<Integer, HPSpell> spells, RemoteCache<String, HPMagic> magic) {
      try {
         CompletableFuture.allOf(characters.clearAsync(), spells.clearAsync(), magic.clearAsync()).get(10, TimeUnit.SECONDS);
      } catch (Exception e) {
         LOGGER.error("Something went wrong clearing stores, the Dark Lord must be around again.", e);
      }
   }

   /**
    * Load characters into the cache
    *
    * @param cache, characters cache
    * @throws Exception
    */
   private void loadCharacters(RemoteCache<Integer, HPCharacter> cache) throws Exception {
      try (BufferedReader br = new BufferedReader(new FileReader(charactersFileName))) {
         String line;
         int id = 0;
         while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            int type = Integer.parseInt(values[0].trim());
            HPCharacter.CharacterType hpType = HPCharacter.CharacterType.values()[type];
            HPCharacter character = new HPCharacter(id, values[1].trim(), values[2].trim(), hpType);
            cache.put(id, character);
            id++;
         }
      }
   }

   /**
    * Load spells into the cache
    *
    * @param cache, spells cache
    * @throws Exception
    */
   private void loadSpells(RemoteCache<Integer, HPSpell> cache) throws Exception {
      try (BufferedReader br = new BufferedReader(new FileReader(spellsFileName))) {
         String line;
         int id = 0;
         while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            HPSpell spell = new HPSpell(id, values[0].trim(), values[1].trim(), values[2].trim());
            cache.put(id, spell);
            id++;
         }
      }
   }
}