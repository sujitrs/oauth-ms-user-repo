package org.sj.msuserrepo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;



@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
@PropertySource(name = "myProperties", value = "authz.properties")
@Log
public class ConfigClientBootstrapConfiguration {
		private String token;
		
		@Value("${readTimeOutInMins}")
		private int READ_TIME_OUT_IN_MINS;
		
		@Value("${uri}")
		private String uri;
		
		@Value("${grantType}")
		private String grantType;
		
		@Value("${clientId}")
		private String clientId;
		
		@Value("${clientSecret}")
		private String clientSecret; 
		
		@Value("${id}")
		private String id;
		
		@Value("${scope}")
		private String scope; 
		
		@Value("${tokenName}")
		private String tokenName;
		
		@Autowired
		private ConfigurableEnvironment environment;
	
		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		
		@PostConstruct
		public void init() {
			 
			 log.log(Level.INFO,"AuthZ properties::Uri={0}",uri);
			 log.log(Level.INFO,"AuthZ properties::grantType={0}",grantType);
			 log.log(Level.INFO,"AuthZ properties::clientId={0}",clientId);
			 log.log(Level.INFO,"AuthZ properties::clientSecret={0}",clientSecret);
			 log.log(Level.INFO,"AuthZ properties::id={0}",id);
			 log.log(Level.INFO,"AuthZ properties::scope={0}",scope);
			 log.log(Level.INFO,"AuthZ properties::tokenName={0}",tokenName);
			
			 ClientCredentialsResourceDetails ccr= new ClientCredentialsResourceDetails();
			  ccr.setAccessTokenUri(uri); 
			  ccr.setGrantType(grantType);
			  ccr.setClientId(clientId);
			  ccr.setClientSecret(clientSecret); 
			  ccr.setId(id);
			  ccr.setScope(Arrays.asList(scope));
			  ccr.setTokenName(tokenName);
			  ccr.setAuthenticationScheme(AuthenticationScheme.header);
			  ccr.setClientAuthenticationScheme(AuthenticationScheme.header);
				 try {

				    final OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(ccr);
				    final OAuth2AccessToken accessToken = oAuth2RestTemplate.getAccessToken();
				    final String token= accessToken.getValue();
					if (token== null) {
						log.log(Level.SEVERE,"Cannot fetch Auth Token from Auth Server"); 
						throw new Exception();
					}
					log.log(Level.INFO,"Token received = {0}",token );
					setToken(token);
				} catch (Exception e) {
					log.log(Level.SEVERE,"Cannot fetch Auth Token from Auth Server. Reason ={0}",e); //Prints stacktrace in log file 
				}
		}

	
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	@Bean
	public ConfigClientProperties configClientProperties() {
		ConfigClientProperties client = new ConfigClientProperties(this.environment);
		client.setEnabled(false);

		return client;
	}

	@Bean
	public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
		ConfigClientProperties clientProperties = configClientProperties();
		ConfigServicePropertySourceLocator configServicePropertySourceLocator = new ConfigServicePropertySourceLocator(
				clientProperties);
		configServicePropertySourceLocator.setRestTemplate(customRestTemplate(clientProperties));

		return configServicePropertySourceLocator;
	}

	private RestTemplate customRestTemplate(ConfigClientProperties clientProperties) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Bearer " + token);
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout((60 * 1000 * READ_TIME_OUT_IN_MINS) ); 
		RestTemplate template = new RestTemplate(requestFactory);
		if (!headers.isEmpty()) {
			template.setInterceptors(
					Arrays.<ClientHttpRequestInterceptor> asList(new GenericRequestHeaderInterceptor(headers)));
		}
		return template;
	}

	public static class GenericRequestHeaderInterceptor implements ClientHttpRequestInterceptor {

		private final Map<String, String> headers;

		public GenericRequestHeaderInterceptor(Map<String, String> headers) {
			this.headers = headers;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			for (Entry<String, String> header : headers.entrySet()) {
				request.getHeaders().add(header.getKey(), header.getValue());
			}
			return execution.execute(request, body);
		}
	}
}
