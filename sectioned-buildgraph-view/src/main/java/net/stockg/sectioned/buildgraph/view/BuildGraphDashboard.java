/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.stockg.sectioned.buildgraph.view;

import com.cloudbees.plugins.flow.BuildFlow;
import com.cloudbees.plugins.flow.BuildFlowDSLExtension;
import org.jenkinsci.plugins.buildgraphview.DownStreamRunDeclarer;
import org.jenkinsci.plugins.buildgraphview.BuildGraph;
import com.cloudbees.plugins.flow.FlowDownStreamRunDeclarer;
import com.cloudbees.plugins.flow.FlowRun;
import com.cloudbees.plugins.flow.FlowRun.JobEdge;
import com.cloudbees.plugins.flow.JobInvocation;
import com.sun.rowset.internal.Row;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.ListView;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import hudson.plugins.parameterizedtrigger.BuildInfoExporterAction;
import hudson.views.BuildButtonColumn;
import hudson.views.JobColumn;
import hudson.views.LastDurationColumn;
import hudson.views.LastFailureColumn;
import hudson.views.LastSuccessColumn;
import hudson.views.ListViewColumn;
import hudson.views.StatusColumn;
import hudson.views.WeatherColumn;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import static net.stockg.sectioned.buildgraph.view.BuildGraphDashboard.LOGGER;
import net.stockg.sectioned.buildgraph.view.ParseFlowDSL;
import org.jenkinsci.plugins.buildgraphview.BuildExecution;
import org.jenkinsci.plugins.buildgraphview.BuildGraph;
import org.jgrapht.DirectedGraph;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.BoundObjectTable.Table;
import org.kohsuke.stapler.export.Exported;

/**
 *
 * @author gstockfisch
 */
public class BuildGraphDashboard  extends ListView {

    public static Logger LOGGER = Logger.getLogger(BuildGraphDashboard.class.getSimpleName());
    private List<String> ACTIVEJOBS = new ArrayList<String>();
    private transient AbstractProject project;
    private transient Run<?, ?> run;
    private Hashtable<String, String> JOBLISTINGS = new Hashtable<String, String>();
    
    @DataBoundConstructor
    public BuildGraphDashboard(String name) {
            super(name);
    }
    
    public List<TopLevelItem> BuildRow(BuildFlow flow, String job, List<TopLevelItem> result){
        //List<TopLevelItem> result = new ArrayList<TopLevelItem>();
        TopLevelItem item = super.getOwnerItemGroup().getItem(job);
        FlowRun flowRun = flow.getLastBuild();
        //DirectedGraph<JobInvocation, FlowRun.JobEdge> currentFlow = flowRun.getJobsGraph();
        
       
        for (Job singleJob : flowRun.getBuildFlow().getAllJobs()){
            if(singleJob.getName().equals(job) ){
                for (DownStreamRunDeclarer declarer : DownStreamRunDeclarer.all()) {
                   List<Run> runs = null;

                   try{
                       runs = declarer.getDownStream(singleJob.getLastBuild());

                   }
                   catch(Exception e){
                       LOGGER.info("Error getting the last build: " + e.getMessage());
                   }

                    for (Run r : runs) {
                        TopLevelItem items = super.getJob(r.getFullDisplayName());
                        items = super.getJob(r.getParent().getName());
                        //List<TopLevelItem> addResult = BuildRow(r.getParent().getName(),addResult);
                        LOGGER.info("Adding more:: " +  r.getParent().getName());
                        result.addAll(BuildRow(flow, r.getParent().getName(),result));
                    }
                }
            }
        }
        return result;
    }
    
    public List<ArrayList<TopLevelItem>> getDownstreamFlowItems(String job) {
        int row = 0;
        List<ArrayList<TopLevelItem>> result = new ArrayList<ArrayList<TopLevelItem>>();
        //result.add(new ArrayList<TopLevelItem>());
        TopLevelItem item = super.getOwnerItemGroup().getItem(job);
        BuildFlow flow = Jenkins.getInstance().getItemByFullName(item.getName(), BuildFlow.class); 
        //BuildFlow buildFlow = new BuildFlow(project.getParent(),project.getRelativeNameFrom(item));
        LOGGER.log(Level.INFO, "getting project!!:: " + project.toString());
        //LOGGER.info( "getting dsl: " + flow.getDsl());
        FlowRun flowRun = flow.getLastBuild();
        DirectedGraph<JobInvocation, FlowRun.JobEdge> currentFlow = flowRun.getJobsGraph();
        Iterator<JobEdge> edge = currentFlow.edgeSet().iterator();
        while(edge.hasNext()){
            LOGGER.info("ITERATING::: " + edge.next().getTarget().getName());
            LOGGER.info("ITERATING222::: " + edge.next().getSource().getName());
        }
        
       
        for (Job singleJob : flowRun.getBuildFlow().getAllJobs()){
            
           for (DownStreamRunDeclarer declarer : DownStreamRunDeclarer.all()) {
               List<Run> runs = null;
               try{
                   runs = declarer.getDownStream(singleJob.getLastBuild());
               }
               catch(Exception e){
                   LOGGER.info("Error getting the last build: " + e.getMessage());
               }
                
                
                for (Run r : runs) {
                    LOGGER.info("maybe one hereDOWNSTREAM: " + r.getDisplayName() + " and " + r.getFullDisplayName());
                    TopLevelItem items = super.getJob(r.getFullDisplayName());
                    result.add(new ArrayList<TopLevelItem>());
                    result.get(row).add(items);
                     //LOGGER.info("nother one here: " + Jenkins.getInstance().getItemByFullName(r.getFullDisplayName()));
                    items = super.getJob(r.getParent().getName());
                    LOGGER.info("maybe one here nameDOWNSTREAM: " + r.getParent().getName());
                    result.get(row).add(super.getJob(r.getParent().getName()));
                    LOGGER.info("I got some nameDOWNSTREAM::::: " + items.getFullDisplayName());
                    
                    //result.get(row).addAll(BuildRow(flow, r.getParent().getName(),result.get(row)));
                    row++;
                    //result.add(super.getOwnerItemGroup().getItem(r.getParent().getName()));
                }
            }
            LOGGER.info( "single JOBBBBBBBBB: " + singleJob.toString());
            
        }
  
        //new ParseFlowDSL().parseFlowScript( flow.getDsl(), LOGGER);
        //run = project.getLastBuild();
        return result;
    }
       
    @Override
    public List<TopLevelItem> getItems() {
        LOGGER.log(Level.INFO, "getting items");
        List<TopLevelItem> result = new ArrayList<TopLevelItem>();
        JOBLISTINGS = new Hashtable<String, String>();
       // List<TopLevelItem> result = new ArrayList<TopLevelItem>();
        
        if(ACTIVEJOBS == null){
            return result;
        }
        for (String names : ACTIVEJOBS) {
            TopLevelItem item = super.getOwnerItemGroup().getItem(names);
            result.add(item);
            LOGGER.log(Level.INFO, "getting items jobs:: " + item.getAllJobs().toString());
            LOGGER.log(Level.INFO, "getting parent:: " + item.getParent().getFullName());
            project = Jenkins.getInstance().getItemByFullName(item.getName(), AbstractProject.class);
            LOGGER.log(Level.INFO, "getting project names:: " + project.getRelativeNameFrom(item));
            LOGGER.log(Level.INFO, "item parent: " + item.getParent());
            BuildFlow flow = Jenkins.getInstance().getItemByFullName(item.getName(), BuildFlow.class); 
            //BuildFlow buildFlow = new BuildFlow(project.getParent(),project.getRelativeNameFrom(item));
            LOGGER.log(Level.INFO, "getting project!!:: " + project.toString());
            LOGGER.info( "getting dsl: " + flow.getDsl());
            new ParseFlowDSL().parseFlowScript( flow.getDsl(), LOGGER);
            run = project.getLastBuild();
            
//            BuildGraph graph = new BuildGraph(project.getLastBuild());
//            LOGGER.info("NAME: " + graph.getStart().toString());
//            try{
//            LOGGER.info("NAME2222: " + graph.getGraph().edgeSet().toString());
//            }
//            catch(Exception e){
//                e.printStackTrace();
//            }
 
//            BuildFlow flow = Jenkins.getInstance().getItemByFullName(item.getName(), BuildFlow.class);
//            try{
//                FlowRun flowRun = new FlowRun(flow);
//                LOGGER.info("flow jobis now: " + flowRun.getJobsGraph().toString());
//                LOGGER.info("flow jobis222 now: " + flowRun.getJobsGraph().getAllEdges(null, null));
//            }
//            catch(Exception e){}
           
            
            try{
                for (DownStreamRunDeclarer declarer : DownStreamRunDeclarer.all()) {
                    List<Run> runs = declarer.getDownStream(run);
                    for (Run r : runs) {
                        LOGGER.info("maybe one here: " + r.getDisplayName() + " and " + r.getFullDisplayName());
                        TopLevelItem itemss = super.getJob(r.getFullDisplayName());
                         //LOGGER.info("nother one here: " + Jenkins.getInstance().getItemByFullName(r.getFullDisplayName()));
                        LOGGER.info("maybe one here name: " + r.getParent().getName());
                        //result.add(super.getOwnerItemGroup().getItem(r.getParent().getName()));
                    }
                }
            }
            catch(Exception e){}
            
            
        
            
            Job parent = run.getParent();
        String name = parent.getFullName();
        List<Run> runs = new ArrayList<Run>();
        
        if (parent instanceof AbstractProject) {
            
            Set<AbstractProject> jobs2 = Jenkins.getInstance().getDependencyGraph().getTransitiveDownstream((AbstractProject) parent);
            // I can't see any reason DependencyGraph require AbstractProject, not Run
            List<AbstractProject> jobs = Jenkins.getInstance().getDependencyGraph().getDownstream((AbstractProject) parent);
            for (Job job : jobs) {
                List<Run> builds = job.getBuilds();
                for (Run b : builds) {
                    Cause.UpstreamCause cause = (Cause.UpstreamCause) b.getCause(Cause.UpstreamCause.class);
                    if (cause != null && cause.getUpstreamProject().equals(name) && cause.getUpstreamBuild() == run.getNumber()) {
                        runs.add(b);
                        LOGGER.info("adding: " + b.getDisplayName());
                        JOBLISTINGS.put(project.getRelativeNameFrom(item),b.getParent().getName());
                        LOGGER.info("working on adding_new: " +b.getParent().getName());
                    }
                }
            }
        }

        
        
        List<AbstractProject<?,?>> listDowns = new ArrayList<AbstractProject<?,?>>();
            
            for (BuildInfoExporterAction action : run.getActions(BuildInfoExporterAction.class)) {
                LOGGER.info("adding some runs");
               listDowns = action.getTriggeredProjects();
               
                //runs.addAll(action.getTriggeredBuilds());
            }
            
            //List<AbstractProject> listDowns = project.getDownstreamProjects();
            if(null != listDowns){
                LOGGER.info("I got some listDowns");
                for (AbstractProject downProjects : listDowns){
                    //JOBLISTINGS.put(project.getRelativeNameFrom(item),downProjects.getFullDisplayName());
                    LOGGER.log(Level.INFO, "Project: "+project.getRelativeNameFrom(item) +" getting project downstream:: " + downProjects.getFullDisplayName());
                    //run = project.getBuild(project.getRelativeNameFrom(item));
                }
            }
            //LOGGER.log(Level.INFO, "last build:: " + run.getDisplayName());
            try{
                computeGraphFrom(run);
            }
            catch(Exception e){}
        
        }
        
        
       
//        Set<AbstractProject> projects = new HashSet<AbstractProject>();
//        project
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
    
    
    @SuppressWarnings("UnusedDeclaration")
    @Exported
    public String getDisplayRows(String job) { 
//		LOGGER.info("getDisplayRows starting");
        //LOGGER.log(Level.INFO,"getDisplayRows starting" + job);
        LOGGER.info("getDisplayRows starting " + job);
        //String result = JOBLISTINGS.get(job);
        BuildGraph graph = new BuildGraph(project.getLastBuild());
        String result = "";
        try{
        result = graph.getGraph().edgeSet().toString();
        }
        catch (Exception e){}
        
        
        
        return result;
    }

    @Exported
    public String getJobUrl(String jobName) {
        LOGGER.log(Level.INFO, "Getting a URL!!!!" );
        
        return "/" + jobName + "/";
    }
    private void computeGraphFrom(Run run) throws ExecutionException, InterruptedException {
        LOGGER.log(Level.INFO, "IN computeGraphFrom " );
        LOGGER.log(Level.INFO, "computeGraphFrom:: " + run.getDisplayName());
        run.getActions(BuildInfoExporterAction.class);
        LOGGER.log(Level.INFO, "runner runner111:: " + run.getDisplayName());
        
        
        
        
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
    /**
     * Traditional column layout before the {@link ListViewColumn} becomes extensible.
     */
    @SuppressWarnings("unchecked")
    private static final List<Class<? extends ListViewColumn>> DEFAULT_COLUMNS =  Arrays.asList(
        StatusColumn.class,
        WeatherColumn.class,
        JobColumn.class,
        LastSuccessColumn.class,
        LastFailureColumn.class,
        LastDurationColumn.class,
        BuildButtonColumn.class
    );
    
}
