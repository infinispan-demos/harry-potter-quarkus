package org.infinispan.hp.model;

import org.infinispan.protostream.annotations.Proto;

import java.util.UUID;

/**
 * Harry Potter saga character
 */
@Proto
public record HPCharacter(UUID id, String name, String bio, CharacterType type) {

   public boolean isAtHogwarts() {
      return type == CharacterType.STUDENT || type == CharacterType.TEACHER;
   }

   public boolean canDoMagic() {
      return type != CharacterType.MUGGLE;
   }
}
