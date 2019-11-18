package org.infinispan.hp.service;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.BasicConfiguration;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

/**
 * Service to cleanup and load application data
 */
@ApplicationScoped
public class Init {
   private static final Logger LOGGER = LoggerFactory.getLogger(Init.class.getName());

   public static final String HP_MAGIC_NAME = "magic";

   @Inject
   RemoteCacheManager cacheManager;

   private static final String CACHE_CONFIG =
         "<infinispan><cache-container>" +
               "<distributed-cache name=\"%s\"></distributed-cache>" +
               "</cache-container></infinispan>";

   /**
    * Listens startup event to load the data
    */
   void onStart(@Observes @Priority(value = 1) StartupEvent ev) {
      LOGGER.info("On start - clean and load");
      String xml = String.format(CACHE_CONFIG, "magic");
      cacheManager.administration().getOrCreateCache(HP_MAGIC_NAME, new XMLStringConfiguration(xml));
   }
}
