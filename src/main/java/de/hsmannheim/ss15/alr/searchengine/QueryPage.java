/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsmannheim.ss15.alr.searchengine;

import jdk.nashorn.internal.runtime.linker.Bootstrap;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 *
 * @author Herbe_000
 */
public class QueryPage extends WebPage {

    String queryInput;
    SearchResultsPage panel;
    private final JavaScriptResourceReference MYPAGE_JS = new JavaScriptResourceReference(QueryPage.class, "bootstrap.js");
    private final CssResourceReference MYPAGE_CSS = new CssResourceReference(QueryPage.class, "bootstrap.css");
    public QueryPage() {
        add(new Label("message", "Searchengine"));

        HelperClass.startIndexRefreshTimer();
        final TextField<String> query = new TextField<String>("query", new PropertyModel<String>(this, "queryInput"));
        query.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        add(query);

        AjaxLink queryButton = new AjaxLink("queryButton") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                panel.doQuery(queryInput);
                target.add(panel.container);

            }
        };
        add(queryButton);

        panel = new SearchResultsPage("results", "");
        panel.setOutputMarkupId(true);
        panel.setOutputMarkupPlaceholderTag(true);
        add(panel);

    }

    public String getQueryInput() {
        return queryInput;
    }

    public void setQueryInput(String queryInput) {
        this.queryInput = queryInput;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        
        response.render(JavaScriptHeaderItem.forReference(MYPAGE_JS));
        response.render(CssHeaderItem.forReference(MYPAGE_CSS));
    }
    
    
    
}
