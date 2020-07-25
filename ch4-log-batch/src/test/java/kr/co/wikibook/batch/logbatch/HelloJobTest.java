package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SpringBatchTest
class HelloJobTest {
	@Autowired
	JobLauncherTestUtils jobTester;

	@Test
	void launch() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addString("dateInUtc", "2019-04-13")
			.addJobParameters(jobTester.getUniqueJobParameters())
			.toJobParameters();

		JobExecution jobExec = jobTester.launchJob(params);

		ExitStatus exitStatus = jobExec.getExitStatus();
		assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
	}
}
