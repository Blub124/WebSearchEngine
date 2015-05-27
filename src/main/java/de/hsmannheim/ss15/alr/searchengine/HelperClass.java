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

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Alex
 */
public class HelperClass {

    private static String indexDir = System.getProperty("user.home") + "\\SearchEngine\\index";
    private static String docsDir = System.getProperty("user.home") + "\\SearchEngine\\files";
    private static LuceneController lController = new DefaultLuceneController(indexDir, docsDir);
    private static Timer indexTimer;
    
    
    
    static void startIndexRefreshTimer() {
       if(indexTimer==null){
            indexTimer=new Timer();
            indexTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                System.out.println("refreshing index");
                lController.refreshIndex();
                System.out.println("index refreshed");
            }

        }, 60000, 600000); //Start refreshing the lucene index after 1 min and then every 10 min.
        }
      

        
        
    }

    public static LuceneController getlController() {
        return lController;
    }

}
