package kr.co.wikibook.batch.healthcheck.metadata;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Bean;

public class RemoveOldJobMetadataConfig {
	@Bean
	public Job removeJobOldMetadataJob(
		JobRepository jobRepository,
		DataSource dataSource,
		BatchProperties properties
	) {
		String tablePrefix = properties.getJdbc().getTablePrefix();
		return new RemoveOldJobMetadataJobFactory(jobRepository, dataSource, tablePrefix)
			.createJob("removeJobOldMetadataJob");
	}
}
