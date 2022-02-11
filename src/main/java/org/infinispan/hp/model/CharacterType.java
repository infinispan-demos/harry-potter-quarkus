package org.infinispan.hp.model;

import org.infinispan.protostream.annotations.ProtoEnumValue;

public enum CharacterType {
   @ProtoEnumValue(number = 1)
   OTHER,
   @ProtoEnumValue(number = 2)
   STUDENT,
   @ProtoEnumValue(number = 3)
   TEACHER,
   @ProtoEnumValue(number = 4)
   MUGGLE
}
