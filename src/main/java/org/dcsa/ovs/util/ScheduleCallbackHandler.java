package org.dcsa.ovs.util;

import lombok.extern.slf4j.Slf4j;
import org.dcsa.ovs.model.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static io.restassured.RestAssured.given;

/**
 * A class calling callBackHandlers when subscriptions are activated because an event has been triggered
 */
@Slf4j
public class ScheduleCallbackHandler extends Thread {


    public ScheduleCallbackHandler(Flux<String> callbackUrls, Schedule schedule) {
        this.schedule= schedule;
        this.callbackUrls=callbackUrls;
    }
    Schedule schedule;
    Flux<String> callbackUrls;


@Override
    public void run (){
        callbackUrls.parallel().runOn(Schedulers.elastic()).doOnNext(callbackUrl -> {
            try {
                given()
                        .contentType("application/json")
                        .body(schedule)
                        .post(callbackUrl);
            } catch (Exception e) {
                log.warn("Failed to connect to "+callbackUrl + " " + e.getMessage());
            }
        }).subscribe();
    }
}
