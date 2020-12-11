package com.example.ags.command;

import com.example.ags.api.CancelCheckInWardCommand;
import com.example.ags.api.CheckInPatientCommand;
import com.example.ags.api.WardCheckedInEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class PatientAggregateListener {

    private final CommandGateway commandGateway;

    @EventHandler
    public void on(WardCheckedInEvent evt) {
        log.info("PatientAggregateListener {}", evt);

        this.commandGateway.send(
                new CheckInPatientCommand(evt.getHospCode() + ":" + evt.getWardCode(), evt.getBedNum(), evt.getHkid(), evt.getName()),
                (commandMessage, commandResultMessage) -> {
                    if (commandResultMessage.isExceptional()) {
                        log.error("Cannot Check-in Patient into Ward, Cancel it now...");
                        PatientAggregateListener.this.commandGateway.send(new CancelCheckInWardCommand(evt.getHkid()));
                    }
                }
        );

    }
}
