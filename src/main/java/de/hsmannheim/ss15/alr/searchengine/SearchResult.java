/*
 * Copyright 2015 Alex.
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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author Alex
 */
public class SearchResult extends Panel {

    private DocumentWrapper doc;
    private ExternalLink titelLink;
    private Label link;
    private MultiLineLabel text;

    public SearchResult(String id, DocumentWrapper doc) {
        super(id);
        this.doc = doc;
        add(titelLink = new ExternalLink("titelLink", doc.getLink(), doc.getTitle()));
        titelLink.add(new AttributeModifier("style", "font-size: 18px;"));
        titelLink.setOutputMarkupId(true);

        add(link = new Label("link", doc.link));
        link.add(new AttributeModifier("style", "font-size: 14px; color: green;"));
        link.setOutputMarkupId(true);

        String highlight = doc.getHighlight();
        if (highlight != null && highlight != "") {
            highlight = highlight + " ...";
        }
        add(text = (MultiLineLabel) new MultiLineLabel("text", highlight).setEscapeModelStrings(false));
        text.add(new AttributeModifier("style", "font-size: 13px; color: #545454;"));
        text.setOutputMarkupId(true);

        this.add(new AttributeModifier("style", "padding-left: 5px; padding-bottom: 1%;"));
        this.setOutputMarkupId(true);
    }

}
