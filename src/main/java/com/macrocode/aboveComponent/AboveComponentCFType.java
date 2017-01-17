package com.macrocode.aboveComponent;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.FeatureManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.MultiSelectCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.security.JiraAuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

public class AboveComponentCFType extends MultiSelectCFType {
    private static final Logger log = LoggerFactory.getLogger(AboveComponentCFType.class);
    private final OptionsManager optionsManager;

    public AboveComponentCFType(OptionsManager optionsManager, CustomFieldValuePersister valuePersister, GenericConfigManager genericConfigManager,
                                JiraBaseUrls jiraBaseUrls, SearchService searchService, FeatureManager featureManager, JiraAuthenticationContext jiraAuthenticationContext) {

        super(optionsManager, valuePersister, genericConfigManager, jiraBaseUrls, searchService, featureManager,jiraAuthenticationContext );
        this.optionsManager = optionsManager;
    }

    @Nonnull
    public Map<String, Object> getVelocityParameters(final Issue issue, final CustomField field, FieldLayoutItem fieldLayoutItem) {
        log.debug("AboveComponentCFType.getVelocityParameters - Begin");

        Map velocityParameters = super.getVelocityParameters(issue, field, fieldLayoutItem);
        velocityParameters.put("field", field);
        velocityParameters.put("issue", issue);

        if (issue != null) {
            // get all options of the custom field
            Options options = this.optionsManager.getOptions(field.getConfigurationSchemes().listIterator().next().getOneAndOnlyConfig());
            velocityParameters.put("options", options);

            // get options assigned to the current issue
            if (issue.getId() != null) {
                Collection<Option> currentOptions = this.getValueFromIssue(field, issue);
                if (currentOptions != null) {
                    List<Long> currentOptionsIds = new ArrayList<>();
                    for(Option option : currentOptions) {
                        currentOptionsIds.add(option.getOptionId());
                    }

                    velocityParameters.put("currentOptions", currentOptions);
                    velocityParameters.put("currentOptionsIds", currentOptionsIds);
                }
            }
        }

        log.debug("AboveComponentCFType.getVelocityParameters - End");

        return velocityParameters;
    }
}