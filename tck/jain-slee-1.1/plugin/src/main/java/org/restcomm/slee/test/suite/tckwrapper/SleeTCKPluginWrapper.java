/***************************************************
 *                                                 *
 *  Restcomm: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 *  Created on 2005-3-26                           *
 *                                                 *
 ***************************************************/

package org.restcomm.slee.test.suite.tckwrapper;

import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
//import javax.ejb.Singleton;
//import javax.ejb.Startup;

import com.opencloud.sleetck.lib.infra.sleeplugin.SleeTCKPlugin;

/**
 * 
 * This class wraps the TCKPlugin which needs the RMI registry to expose 
 * server side test classes to the testing client
 * 
 * @author Francesco Moggia 
 * @author Ivelin Ivanov
 *
 * 
 */

//@Singleton
//@Startup
public class SleeTCKPluginWrapper implements SleeTCKPluginWrapperMBean {
    private static Logger logger = Logger.getLogger(SleeTCKPluginWrapper.class);

    private MBeanServer platformMBeanServer;
    //private ObjectName objectName = null;
    public final static String OBJECT_NAME = "slee:service=SleeTCKWrapper";

    private String tckPluginMBObjName;
    private ObjectInstance tckPluginMBean; 
    private String tckPluginClassName;
    private int rmiRegistryPort;
    private String sleeProviderImpl;   
    private Registry rmiRegistry;

    //@PostConstruct
    public void startService()
    {
        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

        /*
        try {
            //objectName = new ObjectName("SleeTCK:type=" + this.getClass().getName());
            objectName = new ObjectName("slee:service=SleeTCKWrapper");
            platformMBeanServer.registerMBean(this, objectName);
        } catch (Exception e) {
            throw new IllegalStateException("Problem during registration of Monitoring into JMX:" + e);
        }
        */

        this.setTCKPluginClassName("com.opencloud.sleetck.lib.infra.sleeplugin.SleeTCKPlugin");
        this.setTCKPluginMBeanObjectName(":name=SleeTCKPlugin");
        this.setRMIRegistryPort(4099);
        this.setSleeProviderImpl("org.mobicents.slee.container.management.jmx.SleeProviderImpl");

        try {
            //new RegistryImpl(getRMIRegistryPort());

            rmiRegistry = LocateRegistry.createRegistry(getRMIRegistryPort());

        } catch (RemoteException re) {
            logger.info("RMIRegistry failed to bind on port " + getRMIRegistryPort() + ". This is expected in case of redeployment. The exception message is " + re.getMessage());
        }

        try {
            ObjectName objName = new ObjectName(tckPluginMBObjName);

            Logger stdErrLogger = Logger.getLogger("STDERR");
            Level oldLevel = stdErrLogger.getLevel();
            stdErrLogger.setLevel(Level.OFF);

            SleeTCKPlugin tckPlugin = new SleeTCKPlugin(rmiRegistryPort, sleeProviderImpl);
            tckPluginMBean = platformMBeanServer.registerMBean(tckPlugin, objName);

            stdErrLogger.setLevel(oldLevel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@PreDestroy
    public void stopService() {
        try {
            //platformMBeanServer.unregisterMBean(this.objectName);
            platformMBeanServer.unregisterMBean(this.tckPluginMBean.getObjectName());
        } catch (Exception e) {
            throw new IllegalStateException("Problem during unregistration of Monitoring into JMX:" + e);
        }
    }

    public void setTCKPluginClassName(String newClName)
    {
        tckPluginClassName = newClName;
    }

    public String getTCKPluginClassName()
    {
        return tckPluginClassName;
    }

    public void setRMIRegistryPort(int port)
    {
        rmiRegistryPort = port;
    }

    public int getRMIRegistryPort()
    {
        return rmiRegistryPort;
    }

    public void setSleeProviderImpl(String provider)
    {
        sleeProviderImpl = provider;
    }

    public String getSleeProviderImpl()
    {
        return sleeProviderImpl;
    }

    public void setTCKPluginMBeanObjectName(String newMBObjectName)
    {
        tckPluginMBObjName = newMBObjectName;
    }

    public String getTCKPluginMBeanObjectName()
    {
        return tckPluginMBObjName;
    }

}
