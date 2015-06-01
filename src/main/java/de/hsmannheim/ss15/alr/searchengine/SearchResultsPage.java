/*
 * Copyright 2015 Herbe_000.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hsmannheim.ss15.alr.searchengine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.index.StoredDocument;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.wicket.AttributeModifier;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
//import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SearchResultsPage extends Panel {

    List<DocumentWrapper> wrapperList;
    public WebMarkupContainer container;
     ListView<DocumentWrapper> listView;

    public SearchResultsPage(String id, String query) {
        super(id);
        
        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        container.setOutputMarkupPlaceholderTag(true);
        this.setOutputMarkupId(true);
        this.setOutputMarkupPlaceholderTag(true);
        
      
        try {

            List<StoredDocument> searchresults;
            if (query == null || query.isEmpty()) {
                searchresults = new ArrayList<>();
            } else {
                searchresults = HelperClass.getlController().doSearch(query);

            }

            wrapperList = new ArrayList<DocumentWrapper>();

            for (StoredDocument d : searchresults) {
                wrapperList.add(new DocumentWrapper(d));
            }
            listView = new ListView<DocumentWrapper>("repeating", wrapperList) {
                @Override
                protected void populateItem(ListItem<DocumentWrapper> item) {                
                    item.add(new SearchResult("item",item.getModelObject()));
                }
            };
            listView.setOutputMarkupId(true);
            listView.setReuseItems(false);
           
            container.add(listView);

            add(container);
        } catch (IOException ex) {
            Logger.getLogger(SearchResultsPage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(SearchResultsPage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doQuery(String query) {
        List<StoredDocument> searchresults=null;
        if (query == null || query.isEmpty()) {
            searchresults = new ArrayList<>();
        } else {
            try {
                searchresults = HelperClass.getlController().doSearch(query);
            } catch (IOException ex) {
                Logger.getLogger(SearchResultsPage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(SearchResultsPage.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        wrapperList = new ArrayList<DocumentWrapper>();
       
        
        for (StoredDocument d : searchresults) {
            wrapperList.add(new DocumentWrapper(d));
        }
        listView.setList(wrapperList);

    }
}
