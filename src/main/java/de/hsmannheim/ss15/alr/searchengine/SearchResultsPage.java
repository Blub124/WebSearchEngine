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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
//import org.apache.wicket.request.mapper.parameter.PageParameters;
 
public class SearchResultsPage extends WebPage {
    static String indexDir = "target\\index";
    static String docsDir = "target\\files\\";
    private static LuceneController lController = new LuceneController(indexDir, docsDir);
    
    public SearchResultsPage(final PageParameters parameters) {
 
        try {
            //try {
            String query = "";
            int i=1;
            
            if(parameters.containsKey("query")){
                query = parameters.getString("query");
            }
            
            final Label queryInput = new Label("query", "Results for query: " + query);
            add(queryInput);
            
            List<Document> searchresults = lController.doSearchQuery(query);
            add(new ListView<Document>("searchresults", searchresults) {
                @Override
                protected void populateItem(ListItem<Document> item) {
                    item.add(new Label("path", new PropertyModel(item.getModel(), "path")));
                }			
            });
            
            /*            List<Document> results = lController.doSearchQuery(query);
            for (Document d : results) {
            if(i<=10){
            final Label result = new Label("result" + i, "Result: " + d.get("path"));
            add(result);
            }
            
            }
            } catch (IOException ex) {
            Logger.getLogger(SearchResultsPage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
            Logger.getLogger(SearchResultsPage.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        } catch (IOException ex) {
            Logger.getLogger(SearchResultsPage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(SearchResultsPage.class.getName()).log(Level.SEVERE, null, ex);
        }
            


    }
}