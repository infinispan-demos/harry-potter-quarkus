package org.infinispan.hp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.hp.model.HPMagic;
import org.infinispan.hp.service.DataLoader;
import org.infinispan.query.api.continuous.ContinuousQuery;
import org.infinispan.query.api.continuous.ContinuousQueryListener;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.infinispan.client.Remote;

@ServerEndpoint("/harry-potter/hogwarts")
@ApplicationScoped
public class HogwartsMagicWebSocket {
   private static final Logger LOGGER = LoggerFactory.getLogger(HogwartsMagicWebSocket.class.getName());

   @Inject
   @Remote(DataLoader.HP_MAGIC_NAME)
   RemoteCache<String, HPMagic> magic;

   ContinuousQueryListener<String, HPMagic> queryListener;
   Map<String, Session> sessions = new ConcurrentHashMap<>();

   @OnOpen
   public void onOpen(Session session) {
      sessions.put(session.getId(), session);

      if (magic == null) {
         LOGGER.error("Unable to search... Is He-Who-Must-Not-Be-Named around?");
         throw new IllegalStateException("Characters store is null. Try restarting the application");
      }

      LOGGER.info("Hogwarts monitoring session has been opened");
      addListener();
   }

   @OnClose
   public void onClose(Session session) {
      sessions.remove(session.getId());
      LOGGER.info("Hogwarts monitoring has been closed");
      removeListener();
   }

   @OnError
   public void onError(Session session, Throwable throwable) {
      sessions.remove(session.getId());
      LOGGER.error("Hogwarts monitoring session error", throwable);
      removeListener();
   }

   private void addListener() {
      if (queryListener == null) {
         // Create the query. Every character that it's actually performing magic in Hogwarts
         QueryFactory queryFactory = Search.getQueryFactory(magic);

         Query query = queryFactory.create("from hp_monitoring.HPMagic where hogwarts=true");

         // Create a Continuous Query Listener
         ContinuousQueryListener<String, HPMagic> listener = new ContinuousQueryListener<>() {
            @Override
            public void resultJoining(String key, HPMagic value) {
               broadcast(value.getCaster() + " executed " + value.getSpell());
            }
         };
         queryListener = listener;

         // Create a Continuous Query
         ContinuousQuery<String, HPMagic> continuousQuery = Search.getContinuousQuery(magic);

         // Link the query and the listener
         continuousQuery.addContinuousQueryListener(query, listener);
      }
   }
   private void removeListener() {
      if (sessions.isEmpty()) {
         ContinuousQuery<String, HPMagic> continuousQuery = Search.getContinuousQuery(magic);
         continuousQuery.removeContinuousQueryListener(queryListener);
         queryListener = null;
      }
   }

   private void broadcast(String message) {
      sessions.values().forEach(s -> s.getAsyncRemote().sendObject(message, result -> {
         if (result.getException() != null) {
            LOGGER.error("The Dark Lord intercepted the monitoring...", result.getException());
         }
      }));
   }
}
