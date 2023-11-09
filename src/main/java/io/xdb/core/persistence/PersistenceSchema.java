package io.xdb.core.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public class PersistenceSchema {

    private final List<PersistenceTableSchema> globalAttributes = new ArrayList<>();

    private PersistenceSchema() {}

    /**
     * Return an empty persistence schema.
     *
     * @return  a new persistence schema.
     */
    public static PersistenceSchema EMPTY() {
        return new PersistenceSchema();
    }

    /**
     * Create and return a persistence schema with global attributes.
     *
     * @param globalAttributesSchema    the schema of global attributes.
     * @return a new persistence schema.
     */
    public static PersistenceSchema withGlobalAttributes(final PersistenceTableSchema... globalAttributesSchema) {
        return new PersistenceSchema().addGlobalAttributes(globalAttributesSchema);
    }

    /**
     * Update the persistence schema with global attributes and return the new persistence schema.
     *
     * @param globalAttributesSchema    the schema of global attributes.
     * @return the updated persistence schema.
     */
    public PersistenceSchema addGlobalAttributes(final PersistenceTableSchema... globalAttributesSchema) {
        globalAttributes.addAll(Arrays.asList(globalAttributesSchema));
        return this;
    }
}
