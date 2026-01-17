package com.vishal.linkedInProject.APIGateway.filter;

import com.vishal.linkedInProject.APIGateway.JWTService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class AuthenticationFilterGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationFilterGatewayFilterFactory.Config> {

    private final JWTService jwtService;

    public AuthenticationFilterGatewayFilterFactory(JWTService jwtService){
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("Authentication filter executed... {}", exchange.getRequest().getURI());
            final String tokenHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if(tokenHeader == null || !tokenHeader.startsWith("Bearer ")){
                log.error("Missing or invalid Authorization header");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            final String token = tokenHeader.split("Bearer ")[1];
            try{
                String userId = jwtService.getUserIdFromToken(token);
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(r -> r.header("X-User-Id", userId))
                        .build();
                return chain.filter(mutatedExchange);
            } catch (JwtException e) {
                log.error("JWT exception: {}",e.getLocalizedMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    static class Config{

    }
}
