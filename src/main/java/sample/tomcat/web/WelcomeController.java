package sample.tomcat.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    private static final String REPLY_WELCOME = "Reply: Welcome";

    @GetMapping(value = "/welcome")
    public String welcome() throws Exception {
        return REPLY_WELCOME;
    }

    @Value("${info.app.version}")
    private String version;

    @Value("${info.build.number}")
    private String revision;

    @Value("${info.build.time}")
    private String buildDate;

    @GetMapping(value = "/version", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ModelMap version() {
        ModelMap model = new ModelMap();

        model.put("version", version);
        model.put("revision", revision);
        model.put("buildTime", buildDate);

        return model;
    }
}
