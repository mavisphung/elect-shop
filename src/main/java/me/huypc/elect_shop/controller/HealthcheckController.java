package me.huypc.elect_shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import me.huypc.elect_shop.generated.api.HealthcheckApi;
import me.huypc.elect_shop.generated.dto.HealthResponse;

@Slf4j
@RestController
public class HealthcheckController implements HealthcheckApi {

    @Override
    public ResponseEntity<HealthResponse> healthCheck() {
        log.info("Health checked");
        HealthResponse response = new HealthResponse();
        response.setStatus("UP");
        response.setTimestamp(java.time.OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }

}
