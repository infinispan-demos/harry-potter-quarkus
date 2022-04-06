package org.infinispan.hp.model;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.types.java.util.UUIDAdapter;

@AutoProtoSchemaBuilder(schemaPackageName = "hp_monitoring",
      includeClasses = {HPCharacter.class, HPSpell.class, HPMagic.class, CharacterType.class, CustomUUIDAdapter.class})
public interface MonitoringAppSchema extends GeneratedSchema {
}
