package org.infinispan.hp.model;

import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(schemaPackageName = "hp_monitoring",
      includeClasses = {HPCharacter.class, HPSpell.class, HPMagic.class, CharacterType.class})
public interface MonitoringAppContextInitializer extends SerializationContextInitializer {
}
