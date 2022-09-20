package ru.homecredit.jira.listaner;


import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.jira.issue.comments.Comment;
import ru.homecredit.jira.utils.PluginSettingsService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;


@Component
public class IssueCreatedResolvedListener implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(IssueCreatedResolvedListener.class);

    private final PluginSettingsService pluginSettingsService;
    @JiraImport
    private final CommentManager commentManager;
    @JiraImport
    private final EventPublisher eventPublisher;

    @Autowired
    public IssueCreatedResolvedListener(EventPublisher eventPublisher, PluginSettingsService pluginSettingsService,CommentManager commentManager) {
        this.eventPublisher = eventPublisher;
        this.pluginSettingsService = pluginSettingsService;
        this.commentManager = commentManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Enabling plugin");
        eventPublisher.register(this);
    }

    @Override
    public void destroy() throws Exception {
        log.info("Disabling plugin");
        eventPublisher.unregister(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent){
        //log.debug(issueEvent.getIssue().toString());
        Long eventTypeId = issueEvent.getEventTypeId();
        Issue issue = issueEvent.getIssue();
        Comment comm = null;
        if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)) {
            log.info("New Comment Created");
            comm = issueEvent.getComment();
            if (comm == null) {
                log.debug("Expected Comment but none found");
            } else {
                log.debug("Comment Created was " + comm.getId());

                this.updateLastUpdatedCustomField(issueEvent);
            }
            log.debug("End of Comment creation");
        }else if(eventTypeId == EventType.ISSUE_COMMENT_EDITED_ID){
            log.info("Comment EDITED");
            comm = issueEvent.getComment();
            if (comm != null){
                Long lastCommentId = commentManager.getLastComment(issueEvent.getIssue()).getId();
                log.info(String.valueOf(lastCommentId));
                log.info(String.valueOf(comm.getId()));
                Long commId = comm.getId();
                log.info("last comment EDITED");
                this.updateLastUpdatedCustomField(issueEvent);

            }

    }
}


    private void updateLastUpdatedCustomField(IssueEvent issueEvent) {
        log.debug("Comment is:"+issueEvent.getComment().getBody());
        this.updateCustomField(issueEvent, issueEvent.getComment().getBody());
    }

    private void updateCustomField(IssueEvent issueEvent, String newValue) {
        log.debug("updateCustomField work");
        log.debug(this.pluginSettingsService.getPluginSetting("LastComment").toString());
        String cfield = this.pluginSettingsService.getPluginSetting("LastComment").toString();
        log.debug("get customField ID "+cfield);
        if (cfield != null) {
            CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
            CustomField cf = customFieldManager.getCustomFieldObject(cfield);
            log.debug("set last comment");
            cf.updateValue((FieldLayoutItem)null, issueEvent.getIssue(), new ModifiedValue("", newValue),  new DefaultIssueChangeHolder());
        }

    }

}