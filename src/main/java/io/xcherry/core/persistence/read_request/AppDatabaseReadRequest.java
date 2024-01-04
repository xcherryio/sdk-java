package io.xcherry.core.persistence.read_request;

import io.xcherry.core.exception.persistence.AppDatabaseSchemaNotMatchException;
import io.xcherry.core.persistence.schema.PersistenceSchema;
import io.xcherry.core.persistence.schema.app_database.AppDatabaseTableSchema;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseReadRequest {

    private final List<AppDatabaseTableReadRequest> tableReadRequests;

    /**
     * Create an app database read request.
     *
     * @param tableReadRequests a list of {@link AppDatabaseTableReadRequest}.
     * @return  the created app database read request.
     */
    public static AppDatabaseReadRequest create(final AppDatabaseTableReadRequest... tableReadRequests) {
        return AppDatabaseReadRequest.create(Arrays.stream(tableReadRequests).collect(Collectors.toList()));
    }

    /**
     * Create an app database read request.
     *
     * @param tableReadRequests a list of {@link AppDatabaseTableReadRequest}.
     * @return  the created app database read request.
     */
    public static AppDatabaseReadRequest create(final List<AppDatabaseTableReadRequest> tableReadRequests) {
        return new AppDatabaseReadRequest(tableReadRequests);
    }

    public io.xcherry.gen.models.AppDatabaseReadRequest toApiModel(final PersistenceSchema persistenceSchema) {
        if (persistenceSchema == null || persistenceSchema.getAppDatabaseSchema() == null) {
            return null;
        }

        final Map<String, AppDatabaseTableSchema> tableSchemaMap = persistenceSchema
            .getAppDatabaseSchema()
            .getTableSchemaMap();

        tableReadRequests.forEach(tableReadRequest -> {
            if (!tableSchemaMap.containsKey(tableReadRequest.getTableName())) {
                throw new AppDatabaseSchemaNotMatchException(
                    String.format("Table %s is not defined in the persistence schema", tableReadRequest.getTableName())
                );
            }

            tableReadRequest
                .getColumnNames()
                .addAll(tableSchemaMap.get(tableReadRequest.getTableName()).getPrimaryKeyColumns());
        });

        return new io.xcherry.gen.models.AppDatabaseReadRequest()
            .tables(
                tableReadRequests.stream().map(AppDatabaseTableReadRequest::toApiModel).collect(Collectors.toList())
            );
    }
}
