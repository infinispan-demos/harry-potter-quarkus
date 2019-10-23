package org.infinispan.hp.model;

import java.util.Objects;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * Author and Spell magic object
 */
public class HPMagic {
   private final String id;
   private final String caster;
   private final String spell;
   private final Boolean hogwarts;

   @ProtoFactory
   public HPMagic(String id, String caster, String spell, Boolean hogwarts) {
      this.id = id;
      this.caster = caster;
      this.spell = spell;
      this.hogwarts = hogwarts;
   }

   @ProtoField(number = 1)
   public String getId() {
      return id;
   }

   @ProtoField(number = 2)
   public String getCaster() {
      return caster;
   }

   @ProtoField(number = 3)
   public String getSpell() {
      return spell;
   }

   @ProtoField(number = 4)
   public Boolean isHogwarts() {
      return hogwarts;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      HPMagic that = (HPMagic) o;
      return Objects.equals(id, that.id) &&
            Objects.equals(caster, that.caster) &&
            Objects.equals(spell, that.spell) &&
            Objects.equals(hogwarts, that.hogwarts);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, caster, spell, hogwarts);
   }

   @Override
   public String toString() {
      return "HarryPotterMagic{" +
            "id='" + id + '\'' +
            ", caster='" + caster + '\'' +
            ", spell='" + spell + '\'' +
            ", hogwarts='" + hogwarts + '\'' +
            '}';
   }
}
