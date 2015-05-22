/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsmannheim.ss15.alr.searchengine;
import org.apache.wicket.protocol.http.WebApplication;
/**
 *
 * @author Herbe_000
 */
public class WebFrontend extends WebApplication{
    public WebFrontend(){    
    }

    @Override
    protected void init() {
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8"); 
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");  //To change body of generated methods, choose Tools | Templates.
    }
    
    
    @Override
    public Class getHomePage() {
        return QueryPage.class;
    }
    
    
}
