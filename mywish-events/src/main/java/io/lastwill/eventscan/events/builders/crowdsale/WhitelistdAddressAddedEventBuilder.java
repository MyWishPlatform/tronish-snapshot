package io.lastwill.eventscan.events.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedAddressAddedEvent;
import io.mywish.wrapper.ContractEventDefinition;
import org.springframework.stereotype.Component;

@Component
public class WhitelistdAddressAddedEventBuilder extends WhitelistdEventBuilder<WhitelistedAddressAddedEvent> {
    public WhitelistdAddressAddedEventBuilder() {
        super("WhitelistedAddressAdded");
    }

    protected WhitelistedAddressAddedEvent buildInner(final ContractEventDefinition definition, String address, String whitelistedAddress) {
        return new WhitelistedAddressAddedEvent(definition, address, whitelistedAddress);
    }
}
