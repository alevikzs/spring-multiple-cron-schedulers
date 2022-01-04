package com.alevikzs.springmultiplecronschedulers.configs;

import com.alevikzs.springmultiplecronschedulers.properties.ApplicationProperties;
import com.alevikzs.springmultiplecronschedulers.properties.CronProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SchedulingConfig {

    private final ApplicationProperties applicationProperties;

    private final List<ThreadPoolTaskScheduler> cronSchedulers = new ArrayList<>();

    @PostConstruct
    public void init() {
        this.applicationProperties.getCrons().forEach(cronProperties -> {
            final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(cronProperties.getThreads());
            scheduler.setThreadNamePrefix(cronProperties.getName() + "-");
            scheduler.setWaitForTasksToCompleteOnShutdown(true);
            scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            scheduler.setAwaitTerminationSeconds(this.applicationProperties.getAwaitTerminationSeconds());
            scheduler.initialize();

            scheduler.schedule(
                    () -> this.execute(cronProperties),
                    new CronTrigger(cronProperties.getExpression())
            );

            this.cronSchedulers.add(scheduler);
        });
    }

    @PreDestroy
    private void preDestroy() {
        this.cronSchedulers.forEach(ExecutorConfigurationSupport::shutdown);
    }

    @SneakyThrows
    private void execute(final CronProperties cronProperties) {
        log.info("Start cron task [{}]", cronProperties.getName());
        Thread.sleep(this.applicationProperties.getTaskExecutionSeconds() * 1000);
        log.info("End cron task [{}]", cronProperties.getName());
    }

}
