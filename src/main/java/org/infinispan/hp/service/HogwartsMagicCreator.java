package org.infinispan.hp.service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.hp.model.HPCharacter;
import org.infinispan.hp.model.HPMagic;
import org.infinispan.hp.model.HPSpell;
import org.infinispan.client.hotrod.RemoteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.infinispan.client.runtime.Remote;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class HogwartsMagicCreator {
   private static final Logger LOGGER = LoggerFactory.getLogger("HogwartsMagicCreator");

   @Inject
   @Remote(DataLoader.HP_CHARACTERS_NAME)
   RemoteCache<Integer, HPCharacter> characters;

   @Inject
   @Remote(DataLoader.HP_SPELLS_NAME)
   RemoteCache<Integer, HPSpell> spells;

   @Inject
   @Remote(DataLoader.HP_MAGIC_NAME)
   RemoteCache<String, HPMagic> magic;

   private Random randomCharacters = new Random();
   private Random randomSpells = new Random();

   /**
    * Wait 10 seconds (should be enough time for stores to be created if they don't exist. Then perform some magic every
    * 3 seconds
    */
   @Scheduled(every = "3s", delay = 10, delayUnit = TimeUnit.SECONDS)
   void executeMagic() {
      if (characters == null || spells == null || magic == null) {
         LOGGER.error("Unable to perform magic at Hogwarts ... Is You-Know-Who around?");
         throw new IllegalStateException("Characters, Spells or Magic stores are null. Try restarting the application");
      }

      LOGGER.info("... magic is happening ...");

      HPCharacter character = getRandomHpCharacter();
      // skip those dirty muggles
      if (character != null && !character.canDoMagic()) {
         LOGGER.info(character.getName() + " can't perform magic");
         return;
      }

      HPSpell spell = getRandomHpSpell();

      if (character == null || spell == null){
         LOGGER.info("Unable to perform magic, character or spell are null");
         return;
      }

      // Perform magic
      String id = UUID.randomUUID().toString();
      magic.put(id, new HPMagic(id, character.getName(), spell.getName(), character.isAtHogwarts()));
   }

   private HPSpell getRandomHpSpell() {
      Integer spellId = randomSpells.nextInt(spells.size());
      return spells.get(spellId);
   }

   private HPCharacter getRandomHpCharacter() {
      Integer characterId = randomCharacters.nextInt(characters.size());
      return characters.get(characterId);
   }
}
