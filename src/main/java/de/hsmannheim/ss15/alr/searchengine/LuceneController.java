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

import java.io.IOException;
import java.util.List;
import org.apache.lucene.index.StoredDocument;
import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;

/**
 *
 * @author Alex
 */
public abstract class LuceneController {

    protected String indexDir;
    protected String docsDir;
    protected Logger LOGGER;

    public abstract void refreshIndex();

    public abstract List<StoredDocument> doSearch(String queryString) throws IOException, ParseException;
}
