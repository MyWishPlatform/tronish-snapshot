package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.OwnerBalanceChangedEvent;
import io.lastwill.eventscan.messages.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnBean(ExternalNotifier.class)
public class BalanceEventDispatcher {
    @Autowired
    private ExternalNotifier externalNotifier;

    @EventListener
    public void ownerBalanceChangedHandler(final OwnerBalanceChangedEvent event) {
        if (externalNotifier == null) {
            return;
        }
        try {
            externalNotifier.sendPaymentNotify(event.getProduct(), event.getBalance(), PaymentStatus.COMMITTED);

//            commitmentService.waitCommitment(event.getBlock().getHash(), event.getBlock().getNumber().longValue())
//                    .thenApply(committed -> committed ? PaymentStatus.COMMITTED : PaymentStatus.REJECTED)
//                    .thenAccept(status -> externalNotifier.sendPaymentNotify(event.getProduct(), event.getBalance(), status))
//                    .exceptionally(th -> {
//                        log.error("Waiting commitment failed.", th);
//                        return null;
//                    });
        }
        catch (Throwable e) {
            log.error("Sending notification about new balance failed.", e);
        }
    }
}
