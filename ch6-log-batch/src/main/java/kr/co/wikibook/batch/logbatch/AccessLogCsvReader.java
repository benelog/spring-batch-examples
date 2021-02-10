package kr.co.wikibook.batch.logbatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class AccessLogCsvReader implements ItemStreamReader<AccessLog> {
    private final AccessLogLineMapper lineMapper = new AccessLogLineMapper();
    private final Resource resource;
    private BufferedReader bufferedReader;

    public AccessLogCsvReader(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
//        InputStream inputStream;
//        try {
//            inputStream = this.resource.getInputStream();
//        } catch (IOException ex) {
//            throw new ItemStreamException("Error while opening " + this.resource, ex);
//        }
//        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Nullable
    public AccessLog read() throws IOException {
        String line = this.bufferedReader.readLine();
        if (line == null) {
            return null;
        }
        return this.lineMapper.mapLine(line);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    public void close() throws ItemStreamException {
        try {
            this.bufferedReader.close();
        } catch (IOException ex) {
            throw new ItemStreamException("Error while closing " + this.resource, ex);
        }
    }
}