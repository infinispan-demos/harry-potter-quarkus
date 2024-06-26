package org.infinispan.hp.model;

import org.infinispan.protostream.annotations.Proto;

/**
 * Author and Spell magic object
 */
@Proto
public record HPMagic(String id, String caster, String spell, Boolean hogwarts) {

}
