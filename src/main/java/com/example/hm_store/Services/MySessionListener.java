package com.example.hm_store.Services;

import com.example.hm_store.entity.PersistentSession;
import com.example.hm_store.repo.PersistentSessionRepository;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MySessionListener implements HttpSessionListener {
    private PersistentSessionRepository persistentSessionRepository;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {


        String sessionId = se.getSession().getId();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        PersistentSession persistentSession = new PersistentSession();
        persistentSession.setSeries(sessionId);
        persistentSession.setUserName(username);

        persistentSession.setLastUsed(LocalDateTime.now());

        persistentSessionRepository.save(persistentSession);
    }
}
