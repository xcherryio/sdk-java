package io.xdb.core.utils;

import io.xdb.core.persistence.PersistenceSchema;
import io.xdb.core.persistence.PersistenceTableSchema;
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
        AsyncStateConfig asyncStateConfig = new AsyncStateConfig()
            .skipWaitUntil(AsyncState.shouldSkipWaitUntil(state))
            .waitUntilApiTimeoutSeconds(state.getOptions().getWaitUntilApiTimeoutSeconds())
            .executeApiTimeoutSeconds(state.getOptions().getExecuteApiTimeoutSeconds())
            .waitUntilApiRetryPolicy(state.getOptions().getWaitUntilApiRetryPolicy())
            .executeApiRetryPolicy(state.getOptions().getExecuteApiRetryPolicy())
            .stateFailureRecoveryOptions(state.getOptions().getStateFailureRecoveryOptions());

        final PersistenceSchema persistenceSchema = state.getOptions().getPersistenceSchemaToLoad() == null
            ? process.getPersistenceSchema()
            : state.getOptions().getPersistenceSchemaToLoad();
        asyncStateConfig = asyncStateConfig.loadGlobalAttributesRequest(toApiModel(persistenceSchema));

        return asyncStateConfig;
    }

    private static LoadGlobalAttributesRequest toApiModel(final PersistenceSchema persistenceSchema) {
        if (persistenceSchema == null || persistenceSchema.getGlobalAttributes().isEmpty()) {
            return null;
        }

        final LoadGlobalAttributesRequest loadGlobalAttributesRequest = new LoadGlobalAttributesRequest();

        for (final PersistenceTableSchema globalAttribute : persistenceSchema.getGlobalAttributes()) {
            final List<TableColumnDef> columns = new ArrayList<>();

            if (globalAttribute.getPrimaryKeyColumnName() != null) {
                columns.add(new TableColumnDef().dbColumn(globalAttribute.getPrimaryKeyColumnName()));
            }

            for (final String otherColumnName : globalAttribute.getOtherColumnNames()) {
                columns.add(new TableColumnDef().dbColumn(otherColumnName));
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
