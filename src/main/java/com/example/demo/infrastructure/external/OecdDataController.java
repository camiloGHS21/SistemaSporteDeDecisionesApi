package com.example.demo.infrastructure.external;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.external.OecdApiService;
import com.example.demo.domain.external.OecdData;

import reactor.core.publisher.Mono;




@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/oecd-data")
public class OecdDataController {


    private final OecdApiService oecdApiService;


    public OecdDataController(OecdApiService oecdApiService) {
        this.oecdApiService = oecdApiService;
    }



    @GetMapping("/{countryCode}/{year}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public Mono<List<OecdData>> getOecdData(@PathVariable String countryCode, @PathVariable String year) {
        return oecdApiService.getOecdData(countryCode, year);
    }
}