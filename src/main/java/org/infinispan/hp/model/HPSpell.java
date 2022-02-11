package org.infinispan.hp.model;

import java.util.Objects;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * Harry Potter saga spell
 */
public class HPSpell {
   private final String id;
   private final String name;
   private final String type;
   private final String description;

   @ProtoFactory
   public HPSpell(String id, String name, String type, String description) {
      this.id = id;
      this.name = name;
      this.type = type;
      this.description = description;
   }

   @ProtoField(number = 1)
   public String getId() {
      return id;
   }

   @ProtoField(number = 2)
   public String getName() {
      return name;
   }

   @ProtoField(number = 3)
   public String getType() {
      return type;
   }

   @ProtoField(number = 4)
   public String getDescription() {
      return description;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      HPSpell that = (HPSpell) o;
      return Objects.equals(id, that.id)  &&
            Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(description, that.description);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, name, type, description);
   }

   @Override
   public String toString() {
      return "HarryPotterSpell{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", description='" + description + '\'' +
            '}';
   }
}
