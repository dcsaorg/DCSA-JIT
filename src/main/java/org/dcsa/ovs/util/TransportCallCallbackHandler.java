package org.dcsa.ovs.util;

import lombok.extern.slf4j.Slf4j;
import org.dcsa.ovs.model.TransportCall;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static io.restassured.RestAssured.given;

/**
 * A class calling callBackHandlers when subscriptions are activated because an event has been triggered
 */
@Slf4j
public class TransportCallCallbackHandler extends Thread {


    public TransportCallCallbackHandler(Flux<String> callbackUrls, TransportCall transportCall) {
        this.transportCall = transportCall;
        this.callbackUrls=callbackUrls;
    }
    TransportCall transportCall;
    Flux<String> callbackUrls;


@Override
    public void run (){
        callbackUrls.parallel().runOn(Schedulers.elastic()).doOnNext(callbackUrl -> {
            try {
                given()
                        .contentType("application/json")
                        .body(transportCall)
                        .post(callbackUrl);
            } catch (Exception e) {
                log.warn("Failed to connect to "+callbackUrl + " " + e.getMessage());
            }
        }).subscribe();
    }
}
