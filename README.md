# Multiple spring cron schedulers

Here is a simple example of how to define multiple cron triggers based on independent schedulers.
Also implemented graceful shutdown:
1. Wait for the tasks that have already started.
2. Do not wait for tasks that have not yet started.

#### Configuration yml:
```yaml
app:
  await-termination-seconds: 60
  task-execution-seconds: 10
  crons:
    - name: cron1
      threads: 8
      expression: 0/10 * * * * *
    - name: cron2
      threads: 4
      expression: 0/60 * * * * *
    - name: cron3
      threads: 2
      expression: 0/60 0 * * * *
```

#### Configuration class:
```java
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
```
