package com.vitor.events.service;

import com.vitor.events.EventsApplication;
import com.vitor.events.dto.SubscriptionRankingByUser;
import com.vitor.events.dto.SubscriptionRankingItem;
import com.vitor.events.dto.SubscriptionResponse;
import com.vitor.events.exception.EventNotFoundException;
import com.vitor.events.exception.SubscriptionConflictException;
import com.vitor.events.exception.UserIndicadorNotFoundException;
import com.vitor.events.model.Event;
import com.vitor.events.model.Subscription;
import com.vitor.events.model.User;
import com.vitor.events.repo.EventRepo;
import com.vitor.events.repo.SubscriptionRepo;
import com.vitor.events.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.stream.IntStream;

@Service
public class SubscriptionService {

    @Autowired
    private EventRepo evtRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subRepo;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userID) {

        Event evt = evtRepo.findByPrettyName(eventName);
        if (evt == null) {
            throw new EventNotFoundException("Evento " + eventName + " não existe");
        }
        User userRec = userRepo.findByEmail(user.getEmail());
        if (userRec == null) {
            userRec = userRepo.save(user);
        }

        User indicador = null;
        if (userID != null) {
            indicador = userRepo.findById(userID).orElse(null);
            if (indicador == null) {
                throw new UserIndicadorNotFoundException("Usuario " + userID + " indicador não existe");
            }
        }

        Subscription subs = new Subscription();
        subs.setEvent(evt);
        subs.setUser(userRec);
        subs.setIndication(indicador);

        Subscription tmpSub = subRepo.findByEventAndUser(evt, userRec);
        if (tmpSub != null) {
            throw new SubscriptionConflictException("Ja existe incrição para o usuário " + userRec.getName() + " no evento " + evt.getTitle());
        }
        Subscription res = subRepo.save(subs);

        return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription" + res.getEvent().getPrettyName() + "/" + res.getSubscriber().getId());
    }

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
        Event evt = evtRepo.findByPrettyName(prettyName);
        if (evt == null) {
            throw new EventNotFoundException("Ranking do evento " + prettyName + (" não existe"));
        }
        return subRepo.generateRanking(evt.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyname, Integer userId) {
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyname);

        SubscriptionRankingItem item = ranking.stream().filter(i -> i.userID().equals(userId)).findFirst().orElse(null);
        if (item == null) {
            throw new UserIndicadorNotFoundException("Não há incrições com indicação do usuario " + userId);
        }
        Integer posicao = IntStream.range(0, ranking.size())
                .filter(pos -> ranking.get(pos).userID().equals(userId))
                .findFirst().getAsInt();

        return new SubscriptionRankingByUser(item, posicao + 1);
    }
}
