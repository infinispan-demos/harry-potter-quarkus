package org.infinispan.hp.model;

import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;

public class HarryPotterMagicMarshaller implements MessageMarshaller<HPMagic> {

   @Override
   public HPMagic readFrom(ProtoStreamReader reader) throws IOException {
      String id = reader.readString("id");
      String caster = reader.readString("caster");
      String spell = reader.readString("spell");
      boolean h = reader.readBoolean("hogwarts");
      return new HPMagic(id, caster, spell, h);
   }

   @Override
   public void writeTo(ProtoStreamWriter writer, HPMagic magic) throws IOException {
      writer.writeString("id", magic.getId());
      writer.writeString("caster", magic.getCaster());
      writer.writeString("spell", magic.getSpell());
      writer.writeBoolean("hogwarts", magic.isHogwarts());
   }

   @Override
   public Class<? extends HPMagic> getJavaClass() {
      return HPMagic.class;
   }

   @Override
   public String getTypeName() {
      return "quickstart.HPMagic";
   }
}
