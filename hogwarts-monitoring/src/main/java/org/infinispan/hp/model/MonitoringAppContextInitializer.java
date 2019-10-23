package org.infinispan.hp.model;

import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = {HPCharacter.class, HPSpell.class, HPMagic.class, CharacterType.class}, schemaPackageName = "hp_monitoring")
public interface MonitoringAppContextInitializer extends SerializationContextInitializer {
}
