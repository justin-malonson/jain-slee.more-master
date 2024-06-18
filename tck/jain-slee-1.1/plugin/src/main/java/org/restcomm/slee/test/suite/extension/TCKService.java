package org.restcomm.slee.test.suite.extension;

import org.jboss.logging.Logger;
import org.jboss.msc.service.*;
import org.jboss.msc.value.InjectedValue;
import org.restcomm.slee.test.suite.tckwrapper.SleeTCKPluginWrapper;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class TCKService implements Service<TCKService> {

    private final Logger log = Logger.getLogger(TCKService.class);

    public static ServiceName getServiceName() {
        return ServiceName.of("restcomm","sleetck");
    }

    private final InjectedValue<MBeanServer> mbeanServer = new InjectedValue<MBeanServer>();
    public InjectedValue<MBeanServer> getMbeanServer() {
        return mbeanServer;
    }

    private SleeTCKPluginWrapper tckPluginWrapper;

    @Override
    public TCKService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public void start(StartContext context) throws StartException {
        log.info("Starting TCKService");

        tckPluginWrapper = new SleeTCKPluginWrapper();
        registerMBean(tckPluginWrapper, SleeTCKPluginWrapper.OBJECT_NAME);
        tckPluginWrapper.startService();
    }

    @Override
    public void stop(StopContext context) {
        log.info("Stopping TCKService");
        tckPluginWrapper.stopService();
        unregisterMBean(SleeTCKPluginWrapper.OBJECT_NAME);
    }

    private void registerMBean(Object mBean, String name) throws StartException {
        try {
            getMbeanServer().getValue().registerMBean(mBean, new ObjectName(name));
        } catch (Throwable e) {
            throw new StartException(e);
        }
    }

    private void unregisterMBean(String name) {
        try {
            getMbeanServer().getValue().unregisterMBean(new ObjectName(name));
        } catch (Throwable e) {
            log.error("failed to unregister mbean", e);
        }
    }
}
