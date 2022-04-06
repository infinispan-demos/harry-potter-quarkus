package org.infinispan.hp.service;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.hp.model.CharacterType;
import org.infinispan.hp.model.HPCharacter;
import org.infinispan.hp.model.HPMagic;
import org.infinispan.hp.model.HPSpell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service to cleanup and load application data
 */
@ApplicationScoped
public class DataLoader {
   private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class.getName());

   public static final String HP_CHARACTERS_NAME = "characters";
   public static final String HP_SPELLS_NAME = "spells";
   public static final String HP_MAGIC_NAME = "magic";

   @ConfigProperty(name = "characters.filename")
   String charactersFileName;

   @ConfigProperty(name = "spells.filename")
   String spellsFileName;

   @ConfigProperty(name = "clean.magic")
   Boolean clean;

   @Inject
   @Remote(HP_CHARACTERS_NAME)
   RemoteCache<String, HPCharacter> characters;

   @Inject
   @Remote(HP_SPELLS_NAME)
   RemoteCache<String, HPSpell> spells;

   @Inject
   @Remote(HP_MAGIC_NAME)
   RemoteCache<String, HPMagic> magic;

   /**
    * Listens startup event to load the data
    */
   void onStart(@Observes @Priority(value = 1) StartupEvent ev) {
      LOGGER.info("On start - clean and load");
      // Cleanup data
      if (clean) {
         cleanupCaches(characters, spells, magic);
      }

      // Load Ref data
      loadData(characters, spells);
   }

   private void loadData(RemoteCache<String, HPCharacter> characters, RemoteCache<String, HPSpell> spells) {
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

   private void cleanupCaches(RemoteCache<String, HPCharacter> characters, RemoteCache<String, HPSpell> spells, RemoteCache<String, HPMagic> magic) {
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
   private void loadCharacters(RemoteCache<String, HPCharacter> cache) throws Exception {
      InputStream resourceAsStream = this.getClass().getClassLoader()
            .getResourceAsStream(charactersFileName);

      try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream))) {
         String line;
         int id = 0;
         while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            int type = Integer.valueOf(values[0].trim());
            CharacterType hpType = CharacterType.values()[type];
            HPCharacter character = new HPCharacter(UUID.randomUUID(), values[1].trim(), values[2].trim(), hpType);
            cache.put(id + "", character);
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
   private void loadSpells(RemoteCache<String, HPSpell> cache) throws Exception {
      InputStream resourceAsStream = this.getClass().getClassLoader()
            .getResourceAsStream(spellsFileName);
      try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream))) {
         String line;
         int id = 0;
         while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            HPSpell spell = new HPSpell(id + "", values[0].trim(), values[1].trim(), values[2].trim());
            cache.put(id + "", spell);
            id++;
         }
      }
   }
}