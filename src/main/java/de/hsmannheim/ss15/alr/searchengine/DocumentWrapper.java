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

import java.io.Serializable;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.StoredDocument;

/**
 *
 * @author Alex
 */
public class DocumentWrapper implements Serializable{
    
    
    String highlight;
    String link;
    String title;
    
    
    public DocumentWrapper(StoredDocument doc){
        highlight=doc.get("highlight");
        link=doc.get("URL");
        title=doc.get("title");
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }
    

  

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    
}
