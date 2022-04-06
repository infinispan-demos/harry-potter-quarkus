package org.infinispan.hp.model;

import java.util.Objects;
import java.util.UUID;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * Harry Potter saga character
 */
public class HPCharacter {
   private final UUID id;
   private final String name;
   private final String bio;
   private final CharacterType type;

   @ProtoFactory
   public HPCharacter(UUID id, String name, String bio, CharacterType type) {
      this.id = id;
      this.name = name;
      this.bio = bio;
      this.type = type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      HPCharacter character = (HPCharacter) o;
      return  Objects.equals(id, character.id) &&
            Objects.equals(name, character.name) &&
            Objects.equals(bio, character.bio) &&
            Objects.equals(type, character.type);
   }

   @Override
   public String toString() {
      return "Character{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", bio='" + bio + '\'' +
            ", type='" + type + '\'' +
            '}';
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, name, bio, type);
   }

   @ProtoField(number = 1)
   public UUID getId() {
      return id;
   }

   @ProtoField(number = 2)
   public String getName() {
      return name;
   }

   @ProtoField(number = 3)
   public String getBio() {
      return bio;
   }

   @ProtoField(number = 4)
   public CharacterType getType() {
      return type;
   }

   public boolean isAtHogwarts() {
      return type == CharacterType.STUDENT || type == CharacterType.TEACHER;
   }

   public boolean canDoMagic() {
      return type != CharacterType.MUGGLE;
   }
}
