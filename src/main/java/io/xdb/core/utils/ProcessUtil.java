package io.xdb.core.utils;

import io.xdb.core.persistence.schema_to_load.PersistenceSchemaToLoadData;
import io.xdb.core.persistence.schema_to_load.PersistenceTableSchemaToLoadData;
import io.xdb.core.process.Process;
import io.xdb.core.state.AsyncState;
import io.xdb.gen.models.AsyncStateConfig;
import io.xdb.gen.models.LoadGlobalAttributesRequest;
import io.xdb.gen.models.TableColumnDef;
import io.xdb.gen.models.TableReadRequest;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtil {

    /**
     * The default method to get type/id from an objectClass.
     * Only use it when there is no definition of the {@link Process} / {@link AsyncState}
     *
     * @param objectClass
     * @return
     */
    public static String getClassSimpleName(final Class<?> objectClass) {
        return objectClass.getSimpleName();
    }

    public static String getProcessType(final Process process) {
        if (process.getOptions() == null) {
            return getClassSimpleName(process.getClass());
        }
        return process.getOptions().getType();
    }

    public static String getStateId(final AsyncState state) {
        if (state.getOptions() == null) {
            return getClassSimpleName(state.getClass());
        }
        return state.getOptions().getId();
    }

    public static AsyncStateConfig getAsyncStateConfig(final AsyncState state, final Process process) {
        AsyncStateConfig asyncStateConfig = new AsyncStateConfig().skipWaitUntil(AsyncState.shouldSkipWaitUntil(state));

        final PersistenceSchemaToLoadData persistenceSchemaToLoadData = state.getOptions() == null ||
            state.getOptions().getPersistenceSchemaToLoadData() == null
            ? process.getPersistenceSchema().getPersistenceSchemaToLoadData()
            : state.getOptions().getPersistenceSchemaToLoadData();

        asyncStateConfig = asyncStateConfig.loadGlobalAttributesRequest(toApiModel(persistenceSchemaToLoadData));

        if (state.getOptions() == null) {
            return asyncStateConfig;
        }

        asyncStateConfig =
            asyncStateConfig
                .waitUntilApiTimeoutSeconds(state.getOptions().getWaitUntilApiTimeoutSeconds())
                .executeApiTimeoutSeconds(state.getOptions().getExecuteApiTimeoutSeconds())
                .waitUntilApiRetryPolicy(state.getOptions().getWaitUntilApiRetryPolicy())
                .executeApiRetryPolicy(state.getOptions().getExecuteApiRetryPolicy())
                .stateFailureRecoveryOptions(state.getOptions().getStateFailureRecoveryOptions());

        return asyncStateConfig;
    }

    private static LoadGlobalAttributesRequest toApiModel(
        final PersistenceSchemaToLoadData persistenceSchemaToLoadData
    ) {
        if (persistenceSchemaToLoadData == null || persistenceSchemaToLoadData.getGlobalAttributes().isEmpty()) {
            return null;
        }

        final LoadGlobalAttributesRequest loadGlobalAttributesRequest = new LoadGlobalAttributesRequest();

        for (final PersistenceTableSchemaToLoadData globalAttribute : persistenceSchemaToLoadData.getGlobalAttributes()) {
            final List<TableColumnDef> columns = new ArrayList<>();

            for (final String columnName : globalAttribute.getColumnNames()) {
                columns.add(new TableColumnDef().dbColumn(columnName));
            }

            loadGlobalAttributesRequest.addTableRequestsItem(
                new TableReadRequest()
                    .tableName(globalAttribute.getTableName())
                    .lockingPolicy(globalAttribute.getTableReadLockingPolicy())
                    .columns(columns)
            );
        }

        return loadGlobalAttributesRequest;
    }
}
