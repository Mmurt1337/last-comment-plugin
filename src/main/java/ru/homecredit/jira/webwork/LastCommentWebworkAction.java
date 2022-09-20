package ru.homecredit.jira.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.homecredit.jira.utils.PluginSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webwork.action.ServletActionContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;


@Named
public class LastCommentWebworkAction extends JiraWebActionSupport {
    private static final Logger log = LoggerFactory.getLogger(LastCommentWebworkAction.class);
    private final PluginSettingsService pluginSettingsService;

    @Inject
    public LastCommentWebworkAction (PluginSettingsService pluginSettingsService) {
        log.warn("created");
        this.pluginSettingsService = pluginSettingsService;
    }

    public String execute() throws Exception {
        HttpServletRequest req = ServletActionContext.getRequest();
        if ("POST".equalsIgnoreCase(req.getMethod())){
            String cfID = "customfield_"+req.getParameter("cfID");
            log.warn(cfID);
            this.pluginSettingsService.setPluginSetting("LastComment", cfID);
            String cfield = this.pluginSettingsService.getPluginSetting("LastComment").toString();
            log.warn(cfield);
        }
        return super.execute();
    }

    public String doUpdate() {
        this.getRedirect("/secure/action/LastCommentWebworkAction.jspa");
        log.warn("я работаю");
        return null;
    }

}