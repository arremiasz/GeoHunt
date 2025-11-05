package com.geohunt.backend.Services;

import com.geohunt.backend.database.Notifications;
import com.geohunt.backend.database.NotificationsRepository;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationsService {
    @Autowired
    private NotificationsRepository notificationsRepository;

    public List<Notifications> getMyNotifs(Long targetId) {
        List<Notifications> returnable;
        Optional<Notifications> isItPres = notificationsRepository.findByTargetId(targetId);
        if (isItPres.isPresent()) {
            returnable = isItPres.stream().collect(Collectors.toList());
            return returnable;
        }
        return null;
    }
}
