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
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class BuildGraphDashboard  extends View {

    public static Logger LOGGER = Logger.getLogger(BuildGraphDashboard.class.getSimpleName());
    
    @DataBoundConstructor
    public BuildGraphDashboard(String name) {
            super(name);
    }
    
    @Override
    public Collection<TopLevelItem> getItems() {
        List<TopLevelItem> result = new ArrayList<TopLevelItem>();
//        Set<AbstractProject> projects = new HashSet<AbstractProject>();
//        
//        for (AbstractProject project : projects) {
//            Collection<AbstractProject<?, ?>> downstreamProjects = ProjectUtil.getAllDownstreamProjects(project).values();
//            for (AbstractProject<?, ?> abstractProject : downstreamProjects) {
//                result.add(getItem(abstractProject.getName()));
//            }
//        }
//
        return result;
    }
//    @Override
//    public Collection<TopLevelItem> getItems() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public boolean contains(TopLevelItem tli) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onJobRenamed(Item item, String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void submit(StaplerRequest sr) throws IOException, ServletException, Descriptor.FormException {
        sr.bindJSON(this, sr.getSubmittedForm());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
