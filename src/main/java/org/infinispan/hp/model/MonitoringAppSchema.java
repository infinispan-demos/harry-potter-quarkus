package org.infinispan.hp.model;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;

@ProtoSchema(schemaPackageName = "hp_monitoring",
        includeClasses = {HPCharacter.class, HPSpell.class, HPMagic.class, CharacterType.class},
        dependsOn = {
                org.infinispan.protostream.types.java.CommonTypes.class
        })
public interface MonitoringAppSchema extends GeneratedSchema {
}
