package org.infinispan.hp.service;

import java.util.Random;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.hp.model.HPCharacter;
import org.infinispan.hp.model.HPMagic;
import org.infinispan.hp.model.HPSpell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.infinispan.client.runtime.Remote;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class HogwartsMagicCreator {
   private static final Logger LOGGER = LoggerFactory.getLogger(HogwartsMagicCreator.class.getName());

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
   @Scheduled(every = "3s")
   void executeMagic() {
      if (illegalState()) {
         throw new IllegalStateException("Unable to perform magic at Hogwarts ... Is You-Know-Who around?");
      }

      LOGGER.info("... magic is happening ...");

      HPCharacter character = getRandomHpCharacter();
      // skip those dirty muggles
      if (character != null && !character.canDoMagic()) {
         LOGGER.info(character.getName() + " can't perform magic");
         return;
      }

      HPSpell spell = getRandomHpSpell();

      if (character == null || spell == null) {
         LOGGER.info("Unable to perform magic, character or spell are null");
         return;
      }

      // Perform magic
      String id = UUID.randomUUID().toString();
      magic.put(id, new HPMagic(id, character.getName(), spell.getName(), character.isAtHogwarts()));
   }

   private boolean illegalState() {
      if (characters == null || spells == null || magic == null) {
         LOGGER.error("Characters, Spells or Magic stores are null. Expecto Patronum and restart the application");
         return true;
      }

      if (characters.size() == 0 || spells.size() == 0) {
         LOGGER.error("Characters or Spells stores are empty. Lumos and restart the application");
         return true;
      }

      return false;
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
