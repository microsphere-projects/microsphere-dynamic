package io.microsphere.dynamic.jdbc.spring.boot.env;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Set;
import java.util.function.Predicate;

import static io.microsphere.util.ShutdownHookUtils.filterShutdownHookThreads;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class SyncExecutionShutdownHookApplicationListener implements ApplicationListener<ApplicationStartedEvent> {

    private final ConfigurableApplicationContext context;

    private final Predicate<Thread> shutdownHookThreadFilter;

    public SyncExecutionShutdownHookApplicationListener(ConfigurableApplicationContext context, Predicate<Thread> shutdownHookThreadFilter) {
        this.context = context;
        this.shutdownHookThreadFilter = shutdownHookThreadFilter;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ConfigurableApplicationContext eventContext = event.getApplicationContext();
        if (eventContext != context) {
            return;
        }
        Set<Thread> syncShutdownHookThreads = findSyncShutdownHookThreads();
        registerSyncExecutionListener(context, syncShutdownHookThreads);
    }

    private void registerSyncExecutionListener(ConfigurableApplicationContext context, Set<Thread> syncShutdownHookThreads) {
        if (isEmpty(syncShutdownHookThreads)) {
            return;
        }
        context.addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> {
            // Sync execution using Thread#run method
            syncShutdownHookThreads.forEach(Thread::run);
        });
    }

    private Set<Thread> findSyncShutdownHookThreads() {
        return filterShutdownHookThreads(shutdownHookThreadFilter, true);
    }

}
