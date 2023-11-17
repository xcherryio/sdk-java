package io.xdb.core.process;

import io.xdb.core.persistence.PersistenceTableRowToUpsert;
import io.xdb.gen.models.AttributeWriteConflictMode;
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
     * Initialize global attributes when starting a process execution.
     *
     * @param tableName             the name of the table.
     * @param primaryKeyColumns     the column names and values of primary key.
     * @param otherColumns          the column names and values of non-primary key.
     * @param writeConflictMode     the mode to choose when there is a conflict in upsert.
     * @return the updated config.
     */
    public ProcessStartConfig initializeGlobalAttributes(
        final String tableName,
        final Map<String, Object> primaryKeyColumns,
        final Map<String, Object> otherColumns,
        final AttributeWriteConflictMode writeConflictMode
    ) {
        globalAttributesToUpsert.put(
            tableName,
            PersistenceTableRowToUpsert.create(tableName, primaryKeyColumns, otherColumns, writeConflictMode)
        );

        return this;
    }
}
