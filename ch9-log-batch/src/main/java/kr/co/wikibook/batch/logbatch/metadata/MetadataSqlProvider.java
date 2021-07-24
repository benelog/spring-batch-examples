package kr.co.wikibook.batch.logbatch.metadata;

import java.util.Objects;
import org.springframework.batch.core.repository.dao.AbstractJdbcBatchMetadataDao;

class MetadataSqlProvider {

  private final String tablePrefix;

  MetadataSqlProvider(String tablePrefix) {
    this.tablePrefix = Objects.requireNonNullElse(
        tablePrefix, AbstractJdbcBatchMetadataDao.DEFAULT_TABLE_PREFIX
    );
  }

  String selectMaxJobExecutionIdBefore() {
    return "SELECT MAX(job_execution_id) FROM " + this.tablePrefix + "JOB_EXECUTION\n"
        + "WHERE create_time < :createTime";
  }

  String selectMaxJobInstanceId() {
    return "SELECT MAX(job_instance_id) FROM " + this.tablePrefix + "JOB_EXECUTION\n"
        + "WHERE job_execution_id = :jobExecutionId";
  }

  String selectMaxStepExecutionId() {
    return "SELECT MAX (step_execution_id) FROM " + this.tablePrefix + "STEP_EXECUTION\n"
        + "WHERE job_execution_id = :jobExecutionId";
  }

  String deleteJobInstance() {
    return "DELETE FROM " + this.tablePrefix + "JOB_INSTANCE\n"
        + "WHERE job_instance_id <= :maxJobInstanceId";
  }

  String deleteJobExecution() {
    return "DELETE FROM " + this.tablePrefix + "JOB_EXECUTION\n"
        + "WHERE job_execution_id <= :maxJobExecutionId";
  }

  String deleteJobExecutionParams() {
    return "DELETE FROM " + this.tablePrefix + "JOB_EXECUTION_PARAMS\n"
        + "WHERE job_execution_id <= :maxJobExecutionId";
  }

  String deleteJobExecutionContext() {
    return "DELETE FROM " + this.tablePrefix + "JOB_EXECUTION_CONTEXT\n"
        + "WHERE job_execution_id <= :maxJobExecutionId";
  }

  String deleteStepExecution() {
    return "DELETE FROM " + this.tablePrefix + "STEP_EXECUTION\n"
        + "WHERE step_execution_id <= :maxStepExecutionId";
  }

  String deleteStepExecutionContext() {
    return "DELETE FROM " + this.tablePrefix + "STEP_EXECUTION_CONTEXT\n"
        + "WHERE step_execution_id <= :maxStepExecutionId";
  }
}
