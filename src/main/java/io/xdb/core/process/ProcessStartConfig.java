package io.xdb.core.process;

import io.xdb.core.persistence.PersistenceTableRowToUpsert;
import io.xdb.gen.models.ProcessIdReusePolicy;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessStartConfig {

    private final int timeoutSeconds;
    private final ProcessIdReusePolicy processIdReusePolicy;
    /**
     * table name: table row
     */
    private final Map<String, PersistenceTableRowToUpsert> globalAttributesToUpsert = new HashMap<>();

    /**
     * Add a table row as global attributes to upsert and return the updated config.
     *
     * @param tableRow  the table row.
     * @return the updated config.
     */
    public ProcessStartConfig addGlobalAttributesToUpsert(final PersistenceTableRowToUpsert tableRow) {
        globalAttributesToUpsert.put(tableRow.getTableName(), tableRow);
        return this;
    }
}
