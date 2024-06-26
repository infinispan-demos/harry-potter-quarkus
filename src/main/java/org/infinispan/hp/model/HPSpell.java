package org.infinispan.hp.model;

import org.infinispan.protostream.annotations.Proto;

/**
 * Harry Potter saga spell
 */
@Proto
public record HPSpell(String id, String name, String type, String description) {

}
