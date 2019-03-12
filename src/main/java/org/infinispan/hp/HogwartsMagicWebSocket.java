package org.infinispan.hp;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.infinispan.hp.model.HPMagic;
import org.infinispan.hp.service.DataLoader;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.api.continuous.ContinuousQuery;
import org.infinispan.query.api.continuous.ContinuousQueryListener;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.infinispan.client.runtime.Remote;

@ServerEndpoint("/harry-potter/hogwarts")
@ApplicationScoped
public class HogwartsMagicWebSocket {
   private static final Logger LOGGER = LoggerFactory.getLogger(HogwartsMagicWebSocket.class.getName());

   @Inject
   @Remote(DataLoader.HP_MAGIC_NAME)
   RemoteCache<String, HPMagic> magic;

   private Map<Session, ContinuousQueryListener<String, HPMagic>> listeners = new ConcurrentHashMap<>();

   @OnOpen
   public void onOpen(Session session) {
      LOGGER.info("Hogwarts monitoring session has been opened");
      if (magic == null) {
         LOGGER.error("Unable to search... Is He-Who-Must-Not-Be-Named around?");
         throw new IllegalStateException("Characters store is null. Try restarting the application");
      }

      // Create the query. Every character that it's actually performing magic in Hogwarts
      QueryFactory queryFactory = Search.getQueryFactory(magic);
      Query query = queryFactory.from(HPMagic.class)
            .having("hogwarts").eq(true)
            .build();

      // Create a Continuous Query Listener
      ContinuousQueryListener<String, HPMagic> listener = new ContinuousQueryListener<String, HPMagic>() {
         @Override
         public void resultJoining(String key, HPMagic value) {
            try {
               session.getBasicRemote().sendText(value.getCaster() + " executed " + value.getSpell());
            } catch (IOException e) {
               LOGGER.error("The Dark Lord intercepted the monitoring...", e);
            }
         }
      };

      // Create a Continuous Query
      ContinuousQuery<String, HPMagic> continuousQuery = Search.getContinuousQuery(magic);

      // Link the query and the listener
      continuousQuery.addContinuousQueryListener(query, listener);

      // Track a session with a listener to be able to remove the listener when the web-socket is closed or an error happens
      listeners.put(session, listener);
   }

   @OnClose
   public void onClose(Session session) {
      LOGGER.info("Hogwarts monitoring has been closed");
      // Removing the listener is important to avoid memory leaks
      removeListener(session);
   }

   @OnError
   public void onError(Session session, Throwable throwable) {
      LOGGER.error("Hogwarts monitoring session error", throwable);
      // Removing the listener is important to avoid memory leaks
      removeListener(session);
   }

   private void removeListener(Session session) {
      ContinuousQueryListener<String, HPMagic> queryListener = listeners.get(session);
      ContinuousQuery<String, HPMagic> continuousQuery = Search.getContinuousQuery(magic);
      continuousQuery.removeContinuousQueryListener(queryListener);
      listeners.remove(session);
   }
}
