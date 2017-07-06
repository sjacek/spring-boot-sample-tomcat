package sample.tomcat.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory.DEFAULT_PROTOCOL;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON).favorPathExtension(true);
    }

    @Value("${tomcat.ajp.enabled:false}")
    private Boolean tomcatAjpEnabled;

    @Value("${server.port:8081}")
    private int port;

    @Value("${server.redirect-port:-1}")
    private int redirectPort;

    @Value("${tomcat.ajp.port:9093}")
    private int ajpPort;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        if (redirectPort != -1)
            tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
        if (tomcatAjpEnabled)
            tomcat.addAdditionalTomcatConnectors(initiateAjpConnector());

        return tomcat;
    }

    private Connector initiateHttpConnector() {
        Connector connector = new Connector(DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(port);
        connector.setSecure(false);
        connector.setRedirectPort(redirectPort);

        return connector;
    }

    private Connector initiateAjpConnector() {
        Connector connector = new Connector("AJP/1.3");
        connector.setScheme("http");
        connector.setPort(ajpPort);
        connector.setSecure(false);
        connector.setAllowTrace(false);

        return connector;
    }

}
