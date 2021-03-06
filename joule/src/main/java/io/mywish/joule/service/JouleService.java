package io.mywish.joule.service;

import io.mywish.joule.contracts.JouleAPI;
import io.mywish.joule.model.JouleRegistrationState;
import io.mywish.joule.repositories.JouleRegistrationRepository;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JouleService {
    @Value("${io.mywish.joule.contract.address}")
    private String jouleAddress;

    @Autowired
    private Web3j web3j;

    @Autowired
    private JouleAPI jouleAPI;

    @Autowired
    private JouleRegistrationRepository jouleRegistrationRepository;

    @EventListener
    protected void onNewBlockEvent(NewBlockEvent newBlockEvent) {
        List<WrapperTransaction> transactions = newBlockEvent
                .getTransactionsByAddress()
                .get(jouleAddress.toLowerCase());
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        for (WrapperTransaction transaction: transactions) {
            if (transaction.getOutputs().get(0) == null) {
                continue;
            }

            if (!jouleAddress.equalsIgnoreCase(transaction.getOutputs().get(0).getAddress().toLowerCase())) {
                continue;
            }

            TransactionReceipt receipt;
            try {
                receipt = web3j.ethGetTransactionReceipt(transaction.getHash())
                        .send()
                        .getTransactionReceipt()
                        .orElse(null);
            }
            catch (IOException e) {
                log.error("Error on getting receipt for transaction {}.", transaction.getHash(), e);
                continue;
            }
            if (receipt == null) {
                log.error("Null was returned to receipt request for transaction {}.", transaction.getHash());
                continue;
            }

            List<JouleAPI.RegisteredEventResponse> registeredEvents = jouleAPI.getRegisteredEvents(receipt);
            for (JouleAPI.RegisteredEventResponse event : registeredEvents) {
                jouleRegistrationRepository.findByContractAddressAndState(event._address.toLowerCase(), JouleRegistrationState.TX_PUBLISHED)
                        .forEach(jouleRegistration -> jouleRegistrationRepository.updateState(
                                jouleRegistration,
                                JouleRegistrationState.TX_PUBLISHED,
                                JouleRegistrationState.REGISTERED
                        ));
            }

            // TODO: check Invoked event
        }
    }
}
