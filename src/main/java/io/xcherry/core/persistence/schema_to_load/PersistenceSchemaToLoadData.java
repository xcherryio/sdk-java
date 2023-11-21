package io.xcherry.core.persistence.schema_to_load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceSchemaToLoadData {

    private final List<PersistenceTableSchemaToLoadData> globalAttributes = new ArrayList<>();

    /**
     * Create and return an empty persistence schema to load.
     *
     * @return  the created persistence schema to load.
     */
    public static PersistenceSchemaToLoadData EMPTY() {
        return new PersistenceSchemaToLoadData();
    }

    /**
     * Create and return a persistence schema to load with global attributes.
     *
     * @param persistenceTableSchemasToLoad    the table schemas of global attributes to load.
     * @return  the created persistence schema to load.
     */
    public static PersistenceSchemaToLoadData withGlobalAttributes(
        final PersistenceTableSchemaToLoadData... persistenceTableSchemasToLoad
    ) {
        return PersistenceSchemaToLoadData.EMPTY().addGlobalAttributes(persistenceTableSchemasToLoad);
    }

    /**
     * Create and return a persistence schema to load with global attributes.
     *
     * @param persistenceTableSchemasToLoad    the table schemas of global attributes to load.
     * @return  the created persistence schema to load.
     */
    public static PersistenceSchemaToLoadData withGlobalAttributes(
        final List<PersistenceTableSchemaToLoadData> persistenceTableSchemasToLoad
    ) {
        return PersistenceSchemaToLoadData.EMPTY().addGlobalAttributes(persistenceTableSchemasToLoad);
    }

    /**
     * Update the persistence schema with global attributes and return the new persistence schema.
     *
     * @param persistenceTableSchemasToLoad    the table schemas of global attributes to load.
     * @return the updated persistence schema to load.
     */
    public PersistenceSchemaToLoadData addGlobalAttributes(
        final PersistenceTableSchemaToLoadData... persistenceTableSchemasToLoad
    ) {
        return addGlobalAttributes(Arrays.stream(persistenceTableSchemasToLoad).collect(Collectors.toList()));
    }

    /**
     * Update the persistence schema with global attributes and return the new persistence schema.
     *
     * @param persistenceTableSchemasToLoad    the table schemas of global attributes to load.
     * @return the updated persistence schema to load.
     */
    public PersistenceSchemaToLoadData addGlobalAttributes(
        final List<PersistenceTableSchemaToLoadData> persistenceTableSchemasToLoad
    ) {
        globalAttributes.addAll(persistenceTableSchemasToLoad);
        return this;
    }
}
