package org.infinispan.hp;

import static org.infinispan.hp.service.Init.HP_MAGIC_NAME;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.hp.model.HPMagic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.infinispan.client.runtime.Remote;

@Path("/harry-potter/magic")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WizardResource {
   private static final Logger LOGGER = LoggerFactory.getLogger(WizardResource.class.getName());

   @Inject
   @Remote(HP_MAGIC_NAME)
   RemoteCache<String, HPMagic> magicStore;

   @GET
   public Response health() {
      LOGGER.info("Magic store size " + magicStore.size());
      return Response.ok("Wizard Resource is ready for magic!").build();
   }

   @POST
   public Response add(MagicData magic) {
      String id = UUID.randomUUID().toString();
      LOGGER.info("Magic is going on ..." +  magic);
      magicStore.putAsync(id, new HPMagic(id, magic.getCaster(), magic.getSpell(), magic.isInHogwarts()));
      return Response.ok().status(201).build();
   }
}
