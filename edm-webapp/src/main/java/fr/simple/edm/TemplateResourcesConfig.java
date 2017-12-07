package fr.simple.edm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "edm.web.resources")
public class TemplateResourcesConfig {

    @Getter
    @Setter
    @NotNull
    private Boolean concated;

    @Getter
    @Setter
    @NotNull
    private Boolean minified;

    public boolean isConcated() {
        return concated;
    }

    public void setConcated(boolean concated) {
        this.concated = concated;
    }

    public boolean isMinified() {
        return minified;
    }

    public void setMinified(boolean minified) {
        this.minified = minified;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> conf = new HashMap<>();
        conf.put("concated", concated);
        conf.put("minified", minified);
        return conf;
    }
}
