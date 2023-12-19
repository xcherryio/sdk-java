package io.xcherry.core.process;

import io.xcherry.core.encoder.base.DatabaseStringEncoder;
import io.xcherry.core.exception.persistence.AppDatabaseSchemaNotMatchException;
import io.xcherry.core.persistence.schema.AppDatabaseTableSchema;
import io.xcherry.core.persistence.schema.PersistenceSchema;
import io.xcherry.core.persistence.selector.AppDatabaseColumnValueSelector;
import io.xcherry.core.persistence.selector.AppDatabaseRowSelector;
import io.xcherry.core.persistence.selector.AppDatabaseSelector;
import io.xcherry.gen.models.ProcessIdReusePolicy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessStartConfig {

    private final int timeoutSeconds;
    private final ProcessIdReusePolicy processIdReusePolicy;
    private final AppDatabaseSelector appDatabaseSelector;

    public io.xcherry.gen.models.ProcessStartConfig toApiModel(
        final PersistenceSchema persistenceSchema,
        final DatabaseStringEncoder encoder
    ) {
        final AppDatabaseSelector selector = appDatabaseSelector == null
            ? AppDatabaseSelector.create()
            : appDatabaseSelector;

        validatePersistenceSchema(persistenceSchema, selector);

        return new io.xcherry.gen.models.ProcessStartConfig()
            .timeoutSeconds(timeoutSeconds)
            .idReusePolicy(processIdReusePolicy)
            .appDatabaseConfig(selector.toApiModel(encoder));
    }

    private void validatePersistenceSchema(
        final PersistenceSchema persistenceSchema,
        final AppDatabaseSelector appDatabaseSelector
    ) {
        final PersistenceSchema schema = persistenceSchema == null ? PersistenceSchema.EMPTY() : persistenceSchema;

        final Map<String, AppDatabaseTableSchema> schemaTableMap = schema.getAppDatabaseSchema().getTableSchemaMap();
        final Map<String, List<AppDatabaseRowSelector>> selectorTableMap = appDatabaseSelector.getTableToRowsMap();

        schemaTableMap.forEach((tableName, tableSchema) -> {
            final Set<String> schemaPrimaryKeyColumns = tableSchema.getPrimaryKeyColumns();
            final Set<String> schemaOtherColumns = tableSchema.getOtherColumns();

            if (!selectorTableMap.containsKey(tableName)) {
                throw new AppDatabaseSchemaNotMatchException(
                    String.format(
                        "The table %s defined in the persistence schema is not used in the ProcessStartConfig",
                        tableName
                    )
                );
            }

            final List<AppDatabaseRowSelector> rowSelectors = selectorTableMap.get(tableName);

            rowSelectors.forEach(rowSelector -> {
                final Set<String> primaryKeyColumns = rowSelector
                    .getPrimaryKeySelector()
                    .getColumnValueSelectors()
                    .stream()
                    .map(AppDatabaseColumnValueSelector::getColumnName)
                    .collect(Collectors.toSet());
                if (!schemaPrimaryKeyColumns.equals(primaryKeyColumns)) {
                    throw new AppDatabaseSchemaNotMatchException(
                        String.format(
                            "The primary key of table %s defined in the persistence schema does not match the primary key used in the ProcessStartConfig",
                            tableName
                        )
                    );
                }

                final Set<String> otherColumns = rowSelector
                    .getInitialWrite()
                    .stream()
                    .map(AppDatabaseColumnValueSelector::getColumnName)
                    .collect(Collectors.toSet());
                if (!schemaOtherColumns.containsAll(otherColumns)) {
                    throw new AppDatabaseSchemaNotMatchException(
                        String.format(
                            "The columns of table %s defined in the persistence schema does not contain all the columns used in the ProcessStartConfig",
                            tableName
                        )
                    );
                }
            });
        });

        selectorTableMap.forEach((tableName, rowSelectors) -> {
            if (!schemaTableMap.containsKey(tableName)) {
                throw new AppDatabaseSchemaNotMatchException(
                    String.format(
                        "The table %s used in the ProcessStartConfig is not defined in the persistence schema",
                        tableName
                    )
                );
            }
        });
    }
}
