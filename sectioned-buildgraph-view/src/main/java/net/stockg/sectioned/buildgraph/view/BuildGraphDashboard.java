/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.stockg.sectioned.buildgraph.view;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author gstockfisch
 */
public class BuildGraphDashboard  extends ListView {

    public static Logger LOGGER = Logger.getLogger(BuildGraphDashboard.class.getSimpleName());
    private List<String> ACTIVEJOBS = new ArrayList<String>();
    
    @DataBoundConstructor
    public BuildGraphDashboard(String name) {
            super(name);
    }
    
    @Override
    public List<TopLevelItem> getItems() {
        LOGGER.log(Level.INFO, "getting items");
        List<TopLevelItem> result = new ArrayList<TopLevelItem>();
       // List<TopLevelItem> result = new ArrayList<TopLevelItem>();
        
        if(ACTIVEJOBS == null){
            return result;
        }
        for (String names : ACTIVEJOBS) {
            TopLevelItem item = super.getOwnerItemGroup().getItem(names);
            result.add(item);
           
        }
        
       
//        Set<AbstractProject> projects = new HashSet<AbstractProject>();
//        
//        for (AbstractProject project : projects) {
//            Collection<AbstractProject<?, ?>> downstreamProjects = ProjectUtil.getAllDownstreamProjects(project).values();
//            for (AbstractProject<?, ?> abstractProject : downstreamProjects) {
//                result.add(getItem(abstractProject.getName()));
//            }
//        }
//
        LOGGER.log(Level.INFO, "getting items:: " + result.toString());
        return result;
    }

    @Override
    public boolean contains(TopLevelItem tli) {
        LOGGER.log(Level.INFO, "in contains");
        if(tli == null || ACTIVEJOBS == null){
            return false;
        }
        return ACTIVEJOBS.contains(tli.getName());
    }

    @Override
    public void onJobRenamed(Item item, String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void submit(StaplerRequest sr) throws IOException, ServletException, Descriptor.FormException {
        //sr.bindJSON(this, sr.getSubmittedForm());

            LOGGER.log(Level.INFO, "element:: "+sr.getSubmittedForm().toString());
            
            LOGGER.log(Level.INFO, "super:::: "+super.getItems().toString());
       
        LOGGER.log(Level.INFO, "submitted:: "+sr.getSubmittedForm().toString());
        List<TopLevelItem> result = new ArrayList<TopLevelItem>();
        for (Item item : super.getOwnerItemGroup().getItems()) {
            String itemName = item.getName();
            LOGGER.log(Level.INFO, "submitted single name:: "+itemName);
            if(sr.getSubmittedForm().get(itemName).equals(true)){
                if(ACTIVEJOBS == null){
                    ACTIVEJOBS = new ArrayList<String>();
                }
                if(!ACTIVEJOBS.contains(itemName)){
                    ACTIVEJOBS.add(itemName);
                    LOGGER.log(Level.INFO, "adding single name:: "+itemName);
                }
            }
            else if(ACTIVEJOBS != null && ACTIVEJOBS.contains(itemName)){
               ACTIVEJOBS.remove(itemName);
               
            }
        }
        
        
        
       
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Collection<TopLevelItem> getAllItems(){
        LOGGER.log(Level.INFO, "getting ALL items");
        return Collections.emptyList();
    }
    
    @Override
    public Item doCreateItem(StaplerRequest sr, StaplerResponse sr1) throws IOException, ServletException {
        
        Item item = Jenkins.getInstance().doCreateItem(sr, sr1);
		if (item != null) {
			//jobColumns.add(new JobColumn(item.getName(), null, false, true));
			owner.save();
		}
		return item;
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Extension
    public static final class DescriptorImpl extends ViewDescriptor {
            @Override
            public String getDisplayName() {
                    return "Sectioned Buildgraph Dashboard";
            }
    }
    
}
