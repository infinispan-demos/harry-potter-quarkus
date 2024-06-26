package org.infinispan.hp;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.commons.api.query.Query;
import org.infinispan.hp.model.HPMagic;
import org.infinispan.hp.service.DataLoader;
import org.infinispan.query.api.continuous.ContinuousQueryListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/harry-potter/hogwarts")
@ApplicationScoped
public class HogwartsMagicWebSocket {
   @Inject
   @Remote(DataLoader.HP_MAGIC_NAME)
   RemoteCache<String, HPMagic> magic;

   ContinuousQueryListener<String, HPMagic> queryListener;
   Map<String, Session> sessions = new ConcurrentHashMap<>();

   @OnOpen
   public void onOpen(Session session) {
      sessions.put(session.getId(), session);

      if (magic == null) {
         Log.error("Unable to search... Is He-Who-Must-Not-Be-Named around?");
         throw new IllegalStateException("Characters store is null. Try restarting the application");
      }

      Log.info("Hogwarts monitoring session has been opened");
      addListener();
   }

   @OnClose
   public void onClose(Session session) {
      sessions.remove(session.getId());
      Log.info("Hogwarts monitoring has been closed");
      removeListener();
   }

   @OnError
   public void onError(Session session, Throwable throwable) {
      sessions.remove(session.getId());
      Log.error("Hogwarts monitoring session error", throwable);
      removeListener();
   }

   private void addListener() {
      if (queryListener == null) {
         // Create the query. Every character that it's actually performing magic in Hogwarts
         Query<HPMagic> query = magic.query("from hp_monitoring.HPMagic where hogwarts=true");

         // Create a Continuous Query Listener
         ContinuousQueryListener<String, HPMagic> listener = new ContinuousQueryListener<>() {
            @Override
            public void resultJoining(String key, HPMagic value) {
               broadcast(value.caster() + " executed " + value.spell());
            }
         };
         queryListener = listener;

         // Link the query and the listener in the continuous query
         magic.continuousQuery().addContinuousQueryListener(query, listener);
      }
   }
   private void removeListener() {
      if (sessions.isEmpty()) {
         magic.continuousQuery().removeContinuousQueryListener(queryListener);
         queryListener = null;
      }
   }

   private void broadcast(String message) {
      sessions.values().forEach(s -> s.getAsyncRemote().sendObject(message, result -> {
         if (result.getException() != null) {
            Log.error("The Dark Lord intercepted the monitoring...", result.getException());
         }
      }));
   }
}
