/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.stockg.sectioned.buildgraph.view;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;
import javax.servlet.ServletException;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Item doCreateItem(StaplerRequest sr, StaplerResponse sr1) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Extension
    public static final class DescriptorImpl extends ViewDescriptor {
            @Override
            public String getDisplayName() {
                    return "Sectioned Buildgraph Dashboard";
            }
    }
    
}
