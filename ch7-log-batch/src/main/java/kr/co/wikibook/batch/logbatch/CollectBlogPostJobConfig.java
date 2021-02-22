package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import kr.co.wikibook.batch.logbatch.atom.AtomEntry;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = CollectBlogPostJobConfig.JOB_NAME)
public class CollectBlogPostJobConfig {

  public static final String JOB_NAME = "collectBlogPostJob";

  @Bean
  public Job collectBlogPostJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory) {

    return jobFactory
        .get(JOB_NAME)
        .incrementer(new RunIdIncrementer())
        .start(stepFactory.get("collectBlogPostStep")
            .<AtomEntry, BlogPost>chunk(10)
            .reader(atomEntryReader(null))
            .processor(new AtomEntryProcessor())
            .writer(blogPostWriter(null))
            .build())
        .build();
  }

  @Bean
  public StaxEventItemReader<AtomEntry> atomEntryReader(
      @Value("${blog.atom-url}") Resource resource) {

    var unmarshaller = new Jaxb2Marshaller();
    unmarshaller.setClassesToBeBound(AtomEntry.class);
    return new StaxEventItemReaderBuilder<AtomEntry>()
        .name("atomEntryReader")
        .resource(resource)
        .unmarshaller(unmarshaller)
        .addFragmentRootElements("entry")
        .build();
  }

  @Bean
  public StaxEventItemWriter<BlogPost> blogPostWriter(
      @Value("${blog.collection-file}") Resource resource) {

    var marshaller = new Jaxb2Marshaller();
    marshaller.setClassesToBeBound(BlogPost.class);

    return new StaxEventItemWriterBuilder<BlogPost>()
        .name("blogPostWriter")
        .resource(resource)
        .marshaller(marshaller)
        .rootTagName("blog")
        .build();
  }
}