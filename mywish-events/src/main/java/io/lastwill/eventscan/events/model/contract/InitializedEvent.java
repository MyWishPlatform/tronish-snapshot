package io.lastwill.eventscan.events.model.contract;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;

public class InitializedEvent extends BaseEmptyEvent {
    public InitializedEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
