package nl.rug.jbi.jsm.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CSVExporter {
    private final CSVWriter writer;

    public CSVExporter(final File f) throws IOException {
        this.writer = new CSVWriter(new FileWriter(f));
    }

    public void writeDataRow(final List<?> data) {
        final Collection<String> strings = Collections2.transform(data, new Function<Object, String>() {
            @Override
            public String apply(Object o) {
                return Objects.toString(o);
            }
        });
        this.writer.writeNext(strings.toArray(new String[strings.size()]));
    }

    public void close() throws IOException {
        this.writer.close();
    }
}
