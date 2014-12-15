/*
 * The MIT License
 *
 * Copyright (c) 2013, CloudBees, Inc., Nicolas De Loof.
 *                     Cisco Systems, Inc., a California corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.stockg.sectioned.buildgraph.view

import hudson.AbortException
import hudson.console.HyperlinkNote
import hudson.model.*
import hudson.security.ACL
import hudson.slaves.EnvironmentVariablesNodeProperty
import hudson.slaves.NodeProperty
import hudson.util.spring.ClosureScript
import jenkins.model.Jenkins

import org.acegisecurity.context.SecurityContextHolder
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import java.util.concurrent.*
import java.util.logging.Logger

import static hudson.model.Result.FAILURE
import static hudson.model.Result.SUCCESS
import com.cloudbees.plugins.flow.FlowDSL
import com.cloudbees.plugins.flow.JobExecutionFailureException

public class ParseFlowDSL extends FlowDSL{

    
    def void parseFlowScript( String dsl, Logger LOGGER) {
        

        def envMap = [:]
        def getEnvVars = { NodeProperty nodeProperty ->
            if (nodeProperty instanceof EnvironmentVariablesNodeProperty) {
                envMap.putAll( nodeProperty.envVars );
            }
        }
        Jenkins.instance.globalNodeProperties.each(getEnvVars)
        //flowRun.builtOn.nodeProperties.each(getEnvVars)

        // TODO : add restrictions for System.exit, etc ...
        //FlowDelegate flow = new FlowDelegate(flowRun, listener, upstream, envMap)


        // parse the script in such a way that it delegates to the flow object as default
        def cc = new CompilerConfiguration();
        cc.scriptBaseClass = ClosureScript.class.name;
        def ic = new ImportCustomizer()
        ic.addStaticStars(Result.class.name)
        cc.addCompilationCustomizers(ic)

        ClosureScript dslScript = (ClosureScript)new GroovyShell(Jenkins.instance.pluginManager.uberClassLoader,new Binding(),cc).parse(dsl)
        //dslScript.setDelegate(flow);

        try {
            LOGGER.info("running!!!!! : " + dslScript.toString() + " and: " + dslScript.dump())
            
            //dslScript.run()
        } catch(JobExecutionFailureException e) {
            LOGGER.info("failed to compelte.... ")
            //listener.println("flow failed to complete : " + flowRun.state.result)
        }
        catch (AbortException e) {
            LOGGER.info("aborted.... ")
            // aborted should not cause any logging.
            //killRunningJobs(flowRun, listener)
        }
        catch (InterruptedException e) {
            // aborted should not cause any logging.
            //killRunningJobs(flowRun, listener)
        } catch (Exception e) {
            LOGGER.info("failed parsing DSL.... ")
            //listener.error("Failed to run DSL Script")
            //e.printStackTrace(listener.getLogger())
            throw e;
        }
    }
}
