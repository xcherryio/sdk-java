package io.xcherry.core.rpc;

import com.google.common.base.Strings;
import io.xcherry.core.client.BasicClient;
import io.xcherry.core.encoder.base.ObjectEncoder;
import io.xcherry.core.exception.ProcessDefinitionException;
import io.xcherry.core.persistence.read_request.AppDatabaseReadRequest;
import io.xcherry.core.persistence.schema.PersistenceSchema;
import io.xcherry.gen.models.ProcessExecutionRpcRequest;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

@RequiredArgsConstructor
public class RpcInterceptor {

    private final BasicClient basicClient;
    private final String namespace;
    private final String processId;
    private final ObjectEncoder objectEncoder;
    private final PersistenceSchema persistenceSchema;

    @RuntimeType
    public Object intercept(final @AllArguments Object[] args, final @Origin Method method) {
        RpcDefinition.validate(method);

        final RPC rpcAnnotation = method.getAnnotation(RPC.class);

        final Object input = RpcDefinition.getInput(method, args);

        final ProcessExecutionRpcRequest request = new ProcessExecutionRpcRequest()
            .namespace(namespace)
            .processId(processId)
            .rpcName(method.getName())
            .input(objectEncoder.encodeToEncodedObject(input))
            .timeoutSeconds(rpcAnnotation.timeoutInSeconds())
            .appDatabaseReadRequest(getAppDatabaseReadRequest(rpcAnnotation));

        return basicClient.invokeRPC(request, method.getReturnType());
    }

    private io.xcherry.gen.models.AppDatabaseReadRequest getAppDatabaseReadRequest(final RPC rpcAnnotation) {
        final PersistenceSchema persistenceSchemaNotNull = persistenceSchema == null
            ? PersistenceSchema.EMPTY()
            : persistenceSchema;

        if (Strings.isNullOrEmpty(rpcAnnotation.rpcPersistenceReadRequestName())) {
            return persistenceSchemaNotNull.getAppDatabaseReadRequest() == null
                ? null
                : persistenceSchemaNotNull.getAppDatabaseReadRequest().toApiModel(persistenceSchemaNotNull);
        }

        final Map<String, RpcPersistenceReadRequest> rpcPersistenceReadRequestMap = persistenceSchemaNotNull.getRpcPersistenceReadRequestMap();

        if (!rpcPersistenceReadRequestMap.containsKey(rpcAnnotation.rpcPersistenceReadRequestName())) {
            throw new ProcessDefinitionException(
                String.format(
                    "No RpcPersistenceReadRequest contains the name %s",
                    rpcAnnotation.rpcPersistenceReadRequestName()
                )
            );
        }

        final AppDatabaseReadRequest appDatabaseReadRequest = rpcPersistenceReadRequestMap
            .get(rpcAnnotation.rpcPersistenceReadRequestName())
            .getAppDatabaseReadRequest();

        return appDatabaseReadRequest == null ? null : appDatabaseReadRequest.toApiModel(persistenceSchemaNotNull);
    }
}
