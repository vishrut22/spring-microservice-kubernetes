package com.dailycodebuffer.OrderService.external.intercept;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.io.IOException;
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private OAuth2AuthorizedClientManager authorizedClientManager;

    public RestTemplateInterceptor(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.authorizedClientManager = oAuth2AuthorizedClientManager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("Authorization","Bearer "+ authorizedClientManager
                .authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId("internal-client")
                        .principal("internal")
                        .build())
                .getAccessToken().getTokenValue());
        System.out.println("::"+request.getHeaders().get("Authorization"));
        return execution.execute(request,body);
    }
}
