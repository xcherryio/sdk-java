package io.xdb.core.persistence.to_load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceSchemaToLoad {

    private final List<PersistenceTableSchemaToLoad> globalAttributes = new ArrayList<>();

    /**
     * Return an empty persistence schema to load.
     *
     * @return  a new persistence schema to load.
     */
    public static PersistenceSchemaToLoad EMPTY() {
        return new PersistenceSchemaToLoad();
    }

    /**
     * Create and return a persistence schema to load with global attributes.
     *
     * @param persistenceTableSchemasToLoad    the table schemas of global attributes to load.
     * @return a new persistence schema to load.
     */
    public static PersistenceSchemaToLoad withGlobalAttributes(
        final PersistenceTableSchemaToLoad... persistenceTableSchemasToLoad
    ) {
        return new PersistenceSchemaToLoad().addGlobalAttributes(persistenceTableSchemasToLoad);
    }

    /**
     * Create and return a persistence schema to load with global attributes.
     *
     * @param persistenceTableSchemasToLoad    the table schemas of global attributes to load.
     * @return a new persistence schema to load.
     */
    public static PersistenceSchemaToLoad withGlobalAttributes(
        final List<PersistenceTableSchemaToLoad> persistenceTableSchemasToLoad
    ) {
        return new PersistenceSchemaToLoad().addGlobalAttributes(persistenceTableSchemasToLoad);
    }

    /**
     * Update the persistence schema with global attributes and return the new persistence schema.
     *
     * @param persistenceTableSchemasToLoad    the table schemas of global attributes to load.
     * @return the updated persistence schema to load.
     */
    public PersistenceSchemaToLoad addGlobalAttributes(
        final PersistenceTableSchemaToLoad... persistenceTableSchemasToLoad
    ) {
        return addGlobalAttributes(Arrays.stream(persistenceTableSchemasToLoad).collect(Collectors.toList()));
    }

    /**
     * Update the persistence schema with global attributes and return the new persistence schema.
     *
     * @param persistenceTableSchemasToLoad    the table schemas of global attributes to load.
     * @return the updated persistence schema to load.
     */
    public PersistenceSchemaToLoad addGlobalAttributes(
        final List<PersistenceTableSchemaToLoad> persistenceTableSchemasToLoad
    ) {
        globalAttributes.addAll(persistenceTableSchemasToLoad);
        return this;
    }
}
