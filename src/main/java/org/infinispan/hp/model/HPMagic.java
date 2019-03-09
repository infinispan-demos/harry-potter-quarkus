package org.infinispan.hp.model;

import java.util.Objects;

/**
 * Author and Spell magic object
 */
public class HPMagic {
   private final String id;
   private final String caster;
   private final String spell;
   private final boolean hogwarts;

   public HPMagic(String id, String caster, String spell, boolean hogwarts) {
      this.id = id;
      this.caster = caster;
      this.spell = spell;
      this.hogwarts = hogwarts;
   }

   public String getId() {
      return id;
   }

   public String getCaster() {
      return caster;
   }

   public String getSpell() {
      return spell;
   }

   public boolean isHogwarts() {
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
