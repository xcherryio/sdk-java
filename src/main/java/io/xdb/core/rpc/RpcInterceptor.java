package io.xdb.core.rpc;

import io.xdb.core.client.BasicClient;
import io.xdb.core.encoder.base.ObjectEncoder;
import io.xdb.gen.models.ProcessExecutionRpcRequest;
import java.lang.reflect.Method;
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

    @RuntimeType
    public Object intercept(final @AllArguments Object[] args, final @Origin Method method) {
        RpcDefinition.validate(method);

        final RPC rpcAnnotation = method.getAnnotation(RPC.class);

        final Object input = RpcDefinition.getInput(method, args);

        // TODO: set persistence
        final ProcessExecutionRpcRequest request = new ProcessExecutionRpcRequest()
            .namespace(namespace)
            .processId(processId)
            .rpcName(method.getName())
            .input(objectEncoder.encodeToEncodedObject(input))
            .timeoutSeconds(rpcAnnotation.timeoutInSeconds());

        return basicClient.invokeRpc(request, method.getReturnType());
    }
}
