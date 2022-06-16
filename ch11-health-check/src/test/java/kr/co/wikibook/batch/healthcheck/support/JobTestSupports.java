package kr.co.wikibook.batch.healthcheck.support;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.core.task.SyncTaskExecutor;

public class JobTestSupports {
  public static JobLauncherTestUtils getJobLauncherTestUtils(Job job, JobRepository jobRepository) {
    var testUtils = new JobLauncherTestUtils();
    testUtils.setJobRepository(jobRepository);
    testUtils.setJob(job);

    var jobLauncher = new SimpleJobLauncher();
    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.setTaskExecutor(new SyncTaskExecutor()); // 테스트 코드에서는 동기적으로 실행
    testUtils.setJobLauncher(jobLauncher);

    return testUtils;
  }
}
