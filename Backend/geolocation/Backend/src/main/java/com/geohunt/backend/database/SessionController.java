//package com.geohunt.backend.database;
//
//import java.util.HashMap;
//
///**
// * Controls what Sessions are active, and removes expired Sessions
// */
//public class SessionController {
//    private static HashMap<Long,Session> sessionMap = new HashMap<>();
//
//    // Create and add Session
//    public Session createNewSession(Account account){
//        Session newSession = new Session(account);
//        sessionMap.put(newSession.getId(),newSession);
//        return newSession;
//    }
//
//    // Delete Session
//    public void deleteSession(Long id){
//        sessionMap.remove(id);
//    }
//
//    // Get Session by Id
//    public Session getSessionById(Long id){
//        Session session = sessionMap.get(id);
//        return session;
//    }
//
//    // Reset sessionMap
//    public void resetSessionMap(){
//        sessionMap = new HashMap<>();
//    }
//
//}
