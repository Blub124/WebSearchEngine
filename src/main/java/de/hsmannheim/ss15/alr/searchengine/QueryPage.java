/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsmannheim.ss15.alr.searchengine;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 *
 * @author Herbe_000
 */
public class QueryPage extends WebPage{
    
    public QueryPage(final PageParameters parameters){
        add(new Label("message", "Searchengine"));
	final TextField<String> query = new TextField<String>("query", Model.of(""));
        query.setRequired(true);
                
        Form<?> form = new Form<Void>("queryForm") {
 
            @Override
            protected void onSubmit() {
 
		final String queryValue = query.getModelObject();
 
		PageParameters pageParameters = new PageParameters();
		pageParameters.add("query", queryValue);
		//setResponsePage(SearchResultsPage.class, pageParameters);
                setResponsePage(new SearchResultsPage(pageParameters));
 
            }
 
        };
        add(form);
	form.add(query);
    }
}
