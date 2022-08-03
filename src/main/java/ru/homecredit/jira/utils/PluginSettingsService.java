package ru.homecredit.jira.utils;

import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.homecredit.jira.webwork.LastCommentWebworkAction;

import javax.inject.Inject;

@Component
public class PluginSettingsService {
    private static final Logger log = LoggerFactory.getLogger(PluginSettingsService.class);

    @JiraImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public PluginSettingsService(PluginSettingsFactory pluginSettingsFactory) {

        this.pluginSettingsFactory = pluginSettingsFactory;

    }

    public void setPluginSetting(String key, String value) {
        this.pluginSettingsFactory.createGlobalSettings().put("Plugin-LastComment" + key, value);
        log.warn("put settings id:" + value);
    }

    public Object getPluginSetting(String key) {
        return pluginSettingsFactory.createGlobalSettings().get("Plugin-LastComment" + key);
    }

}