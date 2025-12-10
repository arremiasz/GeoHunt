package com.geohunt.backend.Config;

//import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**@Configuration
public class StripeConfig {
    public StripeConfig(@Value("${stripe.apiKey}") String secretKey) {
        Stripe.apiKey = secretKey;
    }
}**/
