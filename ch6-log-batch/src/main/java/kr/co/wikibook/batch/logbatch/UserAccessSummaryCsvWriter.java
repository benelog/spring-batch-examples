package kr.co.wikibook.batch.logbatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.core.io.Resource;

public class UserAccessSummaryCsvWriter implements ItemStreamWriter<UserAccessSummary> {

    private final Resource resource;
    private final UserAccessSummaryLineAggregator lineAggregator = new UserAccessSummaryLineAggregator();
    private BufferedWriter lineWriter;

    public UserAccessSummaryCsvWriter(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            this.lineWriter = Files.newBufferedWriter(Paths.get(resource.getURI()));
        } catch (IOException ex) {
            throw new ItemStreamException("Error while opening " + this.resource, ex);
        }
    }

    @Override
    public void write(List<? extends UserAccessSummary> items) throws Exception {
        for (UserAccessSummary item : items) {
            this.lineWriter.write(lineAggregator.aggregate(item));
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
    }

    @Override
    public void close() throws ItemStreamException {
        try {
            this.lineWriter.close();
        } catch (IOException ex) {
            throw new ItemStreamException("Error while closing " + this.resource, ex);
        }
    }
}
