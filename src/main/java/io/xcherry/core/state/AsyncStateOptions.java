package io.xcherry.core.state;

import com.google.common.base.Strings;
import io.xcherry.core.exception.ProcessDefinitionException;
import io.xcherry.core.exception.persistence.AppDatabaseSchemaNotMatchException;
import io.xcherry.core.exception.persistence.LocalAttributeSchemaNotMatchException;
import io.xcherry.core.persistence.read_request.AppDatabaseReadRequest;
import io.xcherry.core.persistence.read_request.LocalAttributeReadRequest;
import io.xcherry.core.persistence.schema.PersistenceSchema;
import io.xcherry.core.persistence.schema.app_database.AppDatabaseTableSchema;
import io.xcherry.core.utils.ProcessUtil;
import io.xcherry.gen.models.RetryPolicy;
import io.xcherry.gen.models.StateFailureRecoveryOptions;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AsyncStateOptions {

    /**
     * Either stateClass or id must be set
     */
    private final Class<? extends AsyncState> stateClass;
    /**
     * Either stateClass or id must be set
     */
    private final String id;

    private int waitUntilApiTimeoutSeconds;
    private int executeApiTimeoutSeconds;
    private RetryPolicy waitUntilApiRetryPolicy;
    private RetryPolicy executeApiRetryPolicy;
    private StateFailureRecoveryOptions stateFailureRecoveryOptions;
    private AppDatabaseReadRequest appDatabaseReadRequest;
    private LocalAttributeReadRequest localAttributeReadRequest;

    public static AsyncStateOptionsBuilder builder(final Class<? extends AsyncState> stateClass) {
        return builder().stateClass(stateClass);
    }

    private static AsyncStateOptionsBuilder builder() {
        return new AsyncStateOptionsBuilder();
    }

    public String getId() {
        return Strings.isNullOrEmpty(id) ? ProcessUtil.getClassSimpleName(stateClass) : id;
    }

    public AppDatabaseReadRequest getAppDatabaseReadRequest(final PersistenceSchema persistenceSchema) {
        if (persistenceSchema == null || persistenceSchema.getAppDatabaseSchema() == null) {
            return null;
        }

        final Map<String, AppDatabaseTableSchema> tableSchemaMap = persistenceSchema
            .getAppDatabaseSchema()
            .getTableSchemaMap();

        appDatabaseReadRequest
            .getTableReadRequests()
            .forEach(tableReadRequest -> {
                if (!tableSchemaMap.containsKey(tableReadRequest.getTableName())) {
                    throw new AppDatabaseSchemaNotMatchException(
                        String.format(
                            "Table %s is not defined in the persistence schema",
                            tableReadRequest.getTableName()
                        )
                    );
                }

                tableReadRequest
                    .getColumnNames()
                    .addAll(tableSchemaMap.get(tableReadRequest.getTableName()).getPrimaryKeyColumns());
            });

        return appDatabaseReadRequest;
    }

    public LocalAttributeReadRequest getLocalAttributeReadRequest(final PersistenceSchema persistenceSchema) {
        if (persistenceSchema == null || persistenceSchema.getLocalAttributeSchema() == null) {
            return null;
        }

        final Set<String> keysInSchema = persistenceSchema.getLocalAttributeSchema().getKeys();
        final Set<String> keysInReadRequest = localAttributeReadRequest.getKeys();

        if (!keysInSchema.containsAll(keysInReadRequest)) {
            throw new LocalAttributeSchemaNotMatchException(
                "The local attributes defined in the persistence schema do not contain all the keys used in the AsyncStateOptions"
            );
        }

        return localAttributeReadRequest;
    }

    public void validate() {
        if (stateClass == null && Strings.isNullOrEmpty(id)) {
            throw new ProcessDefinitionException("AsyncStateOptions: either stateClass or id must be set.");
        }
    }
}
