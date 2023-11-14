package io.xdb.core.client;

import com.google.common.collect.ImmutableMap;
import io.xdb.core.exception.persistence.GlobalAttributeSchemaNotMatchException;
import io.xdb.core.persistence.PersistenceTableRowToUpsert;
import io.xdb.core.persistence.schema.PersistenceSchema;
import io.xdb.core.persistence.schema.PersistenceTableSchema;
import io.xdb.core.process.Process;
import io.xdb.core.process.ProcessOptions;
import io.xdb.core.registry.Registry;
import io.xdb.core.state.AsyncState;
import io.xdb.core.utils.ProcessUtil;
import io.xdb.gen.models.GlobalAttributeConfig;
import io.xdb.gen.models.GlobalAttributeTableConfig;
import io.xdb.gen.models.LocalQueueMessage;
import io.xdb.gen.models.ProcessExecutionDescribeResponse;
import io.xdb.gen.models.ProcessExecutionStartRequest;
import io.xdb.gen.models.ProcessExecutionStopType;
import io.xdb.gen.models.ProcessStartConfig;
import io.xdb.gen.models.PublishToLocalQueueRequest;
import io.xdb.gen.models.TableColumnValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Client {

    private final Registry registry;
    private final ClientOptions clientOptions;

    private final BasicClient basicClient;

    public Client(final Registry registry, final ClientOptions clientOptions) {
        this.registry = registry;
        this.clientOptions = clientOptions;
        this.basicClient = new BasicClient(clientOptions);
    }

    /**
     * Start a new process execution.
     *
     * @param process       the target process to start.
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param input         the input data to be provided for the execution.
     * @return              a unique identifier for the process execution.
     */
    public String startProcess(final Process process, final String processId, final Object input) {
        final String processType = ProcessUtil.getProcessType(process);
        return startProcessInternal(processType, processId, input);
    }

    /**
     * Caution: if you intend to override certain process options, utilize the {@link Client#startProcess(Process, String, Object)} method
     * Start a new process execution.
     *
     * @param processClass  the class of the target process to start.
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param input         the input data to be provided for the execution.
     * @return              a unique identifier for the process execution.
     */
    public String startProcess(
        final Class<? extends Process> processClass,
        final String processId,
        final Object input
    ) {
        final String processType = ProcessUtil.getClassSimpleName(processClass);
        return startProcessInternal(processType, processId, input);
    }

    /**
     * Stop a process execution as TERMINATED.
     *
     * @param processId a unique identifier used to differentiate between different executions of the same process type.
     */
    public void stopProcess(final String processId) {
        stopProcess(processId, ProcessExecutionStopType.TERMINATE);
    }

    /**
     * Stop a process execution.
     *
     * @param processId a unique identifier used to differentiate between different executions of the same process type.
     * @param stopType  specify how the process execution should be stopped, either as TERMINATED or FAILED.
     */
    public void stopProcess(final String processId, final ProcessExecutionStopType stopType) {
        basicClient.stopProcess(clientOptions.getNamespace(), processId, stopType);
    }

    /**
     * Publish message(s) to the local queue for consumption by the process execution.
     *
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param queueNames    queue names for categorizing message types.
     */
    public void publishToLocalQueue(final String processId, final String... queueNames) {
        final LocalQueueMessage[] messages = Arrays
            .stream(queueNames)
            .map(queueName -> new LocalQueueMessage().queueName(queueName))
            .toArray(LocalQueueMessage[]::new);

        publishToLocalQueue(processId, messages);
    }

    /**
     * Publish a message to the local queue for consumption by the process execution.
     *
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param queueName     queue name for categorizing message types.
     * @param payload       payload of the message.
     */
    public void publishToLocalQueue(final String processId, final String queueName, final Object payload) {
        publishToLocalQueue(processId, queueName, "", payload);
    }

    /**
     * Publish a message to the local queue for consumption by the process execution.
     *
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param queueName     queue name for categorizing message types.
     * @param dedupId       UUID to uniquely distinguish different messages. If not specified, the server will generate a UUID instead.
     * @param payload       payload of the message.
     */
    public void publishToLocalQueue(
        final String processId,
        final String queueName,
        final String dedupId,
        final Object payload
    ) {
        final LocalQueueMessage message = new LocalQueueMessage()
            .queueName(queueName)
            .dedupId(dedupId)
            .payload(clientOptions.getObjectEncoder().encodeToEncodedObject(payload));

        publishToLocalQueue(processId, message);
    }

    /**
     * Get information about a specific process execution.
     *
     * @param processId a unique identifier used to differentiate between different executions of the same process type.
     * @return          information about the process execution.
     */
    public ProcessExecutionDescribeResponse describeCurrentProcessExecution(final String processId) {
        return basicClient.describeCurrentProcessExecution(clientOptions.getNamespace(), processId);
    }

    private String startProcessInternal(final String processType, final String processId, final Object input) {
        final Process process = registry.getProcess(processType);
        final ProcessOptions processOptions = process.getOptions() == null
            ? ProcessOptions.builder(process.getClass()).build()
            : process.getOptions();

        final ProcessExecutionStartRequest request = new ProcessExecutionStartRequest()
            .namespace(clientOptions.getNamespace())
            .processId(processId)
            .processType(processType)
            .workerUrl(clientOptions.getWorkerUrl())
            .startStateInput(clientOptions.getObjectEncoder().encodeToEncodedObject(input))
            .processStartConfig(toApiModel(process.getPersistenceSchema(), processOptions.getProcessStartConfig()));

        if (process.getStateSchema() != null && process.getStateSchema().getStartingState() != null) {
            final AsyncState startingState = process.getStateSchema().getStartingState();
            request
                .startStateId(ProcessUtil.getStateId(startingState))
                .startStateConfig(ProcessUtil.getAsyncStateConfig(startingState, process));
        }

        return basicClient.startProcess(request);
    }

    private ProcessStartConfig toApiModel(
        final PersistenceSchema persistenceSchema,
        final io.xdb.core.process.ProcessStartConfig processStartConfig
    ) {
        final Map<String, PersistenceTableRowToUpsert> globalAttributesToUpsert = processStartConfig == null
            ? ImmutableMap.of()
            : processStartConfig.getGlobalAttributesToUpsert();

        validateThePersistenceSchema(persistenceSchema, globalAttributesToUpsert);

        if (processStartConfig == null) {
            return null;
        }

        final GlobalAttributeConfig globalAttributeConfig;
        if (globalAttributesToUpsert.isEmpty()) {
            globalAttributeConfig = null;
        } else {
            globalAttributeConfig = new GlobalAttributeConfig();

            for (final PersistenceTableRowToUpsert tableRowToUpsert : globalAttributesToUpsert.values()) {
                globalAttributeConfig.addTableConfigsItem(
                    new GlobalAttributeTableConfig()
                        .tableName(tableRowToUpsert.getTableName())
                        .primaryKey(toApiModel(tableRowToUpsert.getPrimaryKeyColumns()))
                        .initialWrite(toApiModel(tableRowToUpsert.getOtherColumns()))
                        .initialWriteMode(tableRowToUpsert.getWriteConflictMode())
                );
            }
        }

        return new ProcessStartConfig()
            .timeoutSeconds(processStartConfig.getTimeoutSeconds())
            .idReusePolicy(processStartConfig.getProcessIdReusePolicy())
            .globalAttributeConfig(globalAttributeConfig);
    }

    private void validateThePersistenceSchema(
        final PersistenceSchema persistenceSchema,
        final Map<String, PersistenceTableRowToUpsert> globalAttributesToUpsert
    ) {
        final PersistenceSchema schema = persistenceSchema == null ? PersistenceSchema.EMPTY() : persistenceSchema;

        schema
            .getGlobalAttributes()
            .forEach((tableName, tableSchema) -> {
                if (!globalAttributesToUpsert.containsKey(tableName)) {
                    throw new GlobalAttributeSchemaNotMatchException(
                        String.format(
                            "The table %s in the persistence schema is not defined in the ProcessStartConfig",
                            tableName
                        )
                    );
                }

                final PersistenceTableRowToUpsert tableRowToUpsert = globalAttributesToUpsert.get(tableName);

                tableSchema
                    .getPrimaryKeyColumns()
                    .forEach((columnName, columnSchema) -> {
                        if (!tableRowToUpsert.getPrimaryKeyColumns().containsKey(columnName)) {
                            throw new GlobalAttributeSchemaNotMatchException(
                                String.format(
                                    "The primary key column %s of the table %s in the persistence schema is not defined in the ProcessStartConfig",
                                    columnName,
                                    tableName
                                )
                            );
                        }
                    });
            });

        globalAttributesToUpsert.forEach((tableName, tableRowToUpsert) -> {
            if (!schema.getGlobalAttributes().containsKey(tableName)) {
                throw new GlobalAttributeSchemaNotMatchException(
                    String.format(
                        "The table %s defined in the ProcessStartConfig does not exist in the persistence schema",
                        tableName
                    )
                );
            }

            final PersistenceTableSchema tableSchema = schema.getGlobalAttributes().get(tableName);

            tableRowToUpsert
                .getPrimaryKeyColumns()
                .forEach((columnName, value) -> {
                    if (!tableSchema.getPrimaryKeyColumns().containsKey(columnName)) {
                        throw new GlobalAttributeSchemaNotMatchException(
                            String.format(
                                "The primary key column %s of the table %s in the ProcessStartConfig does not exist in the persistence schema",
                                columnName,
                                tableName
                            )
                        );
                    }
                });

            tableRowToUpsert
                .getOtherColumns()
                .forEach((columnName, value) -> {
                    if (!tableSchema.getOtherColumns().containsKey(columnName)) {
                        throw new GlobalAttributeSchemaNotMatchException(
                            String.format(
                                "The column %s of the table %s in the ProcessStartConfig does not exist in the persistence schema",
                                columnName,
                                tableName
                            )
                        );
                    }
                });
        });
    }

    private List<TableColumnValue> toApiModel(final Map<String, Object> columnNameToValueMap) {
        final List<TableColumnValue> columns = new ArrayList<>();

        columnNameToValueMap.forEach((k, v) -> {
            columns.add(
                new TableColumnValue()
                    .dbColumn(k)
                    .dbQueryValue(clientOptions.getDatabaseStringEncoder().encodeToString(v))
            );
        });

        return columns;
    }

    /**
     * Publish message(s) to the local queue for consumption by the process execution.
     *
     * @param processId     a unique identifier used to differentiate between different executions of the same process type.
     * @param messages      messages.
     */
    private void publishToLocalQueue(final String processId, final LocalQueueMessage... messages) {
        final PublishToLocalQueueRequest request = new PublishToLocalQueueRequest()
            .namespace(clientOptions.getNamespace())
            .processId(processId)
            .messages(Arrays.stream(messages).collect(Collectors.toList()));

        basicClient.publishToLocalQueue(request);
    }
}
