package org.infinispan.hp.model;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.infinispan.protostream.MessageMarshaller;

@ApplicationScoped
public class MarshallerConfiguration {

   @Produces
   MessageMarshaller magicMarshaller() {
      return new HarryPotterMagicMarshaller();
   }
}
