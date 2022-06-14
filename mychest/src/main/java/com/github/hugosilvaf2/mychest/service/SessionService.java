package com.github.hugosilvaf2.mychest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.hugosilvaf2.mychest.session.Session;

import org.bukkit.entity.Player;

public class SessionService {
    private List<Session> sessions;

    public SessionService() {
        this.sessions = new ArrayList<>();
    }

    public boolean hasSessionByID(int id) {
        return getSessionByID(id).isPresent();
    }

    public Optional<Session> getSessionByViewer(Player player) {
        
        return sessions.stream().filter(c -> c.getViewers().stream().filter(b -> b.getUniqueId().toString().equals(player.getUniqueId().toString())).findFirst().isPresent()).findFirst();
    }
    
    public Optional<Session> getSessionByID(int id) {
     for(Session c : sessions) {
         if(c.getChest().getID() == id) {
             return Optional.ofNullable(c);
         }
     }
        return Optional.ofNullable(null);
    }

    public SessionService addSession(Session session) {
        this.sessions.add(session);
        return this;
    }

    public SessionService removeSessionByID(int id) {
        Optional<Session> session = getSessionByID(id);
        if(session.isPresent()) {
            sessions.remove(session.get());
        }
        return this;
    }

    public int sessionsSize() {
        return this.sessions.size();
    }

    public List<Session> gSessions() {
        return this.sessions;
    }
    
}
