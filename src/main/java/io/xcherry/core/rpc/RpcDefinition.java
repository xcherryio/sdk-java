package io.xcherry.core.rpc;

import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.exception.ProcessDefinitionException;
import io.xcherry.core.exception.RpcException;
import io.xcherry.core.persistence.Persistence;
import io.xcherry.core.process.Process;
import io.xcherry.core.utils.ProcessUtil;
import java.lang.reflect.Method;

public final class RpcDefinition {

    public static final int RPC_METHOD_PARAMETERS_FULL_LENGTH = 4;
    public static final int RPC_METHOD_PARAMETERS_INPUT_INDEX = 1;

    @FunctionalInterface
    public interface RpcMethod<I, O> {
        O execute(
            final Context context,
            final I input,
            final Persistence persistence,
            final Communication communication
        );
    }

    @FunctionalInterface
    public interface RpcMethodNoOutput<I> {
        void execute(
            final Context context,
            final I input,
            final Persistence persistence,
            final Communication communication
        );
    }

    @FunctionalInterface
    public interface RpcMethodNoInput<O> {
        O execute(final Context context, final Persistence persistence, final Communication communication);
    }

    @FunctionalInterface
    public interface RpcMethodNoInputNoOutput {
        void execute(final Context context, final Persistence persistence, final Communication communication);
    }

    public static void validate(final Method rpcMethod) {
        if (!rpcMethod.isAnnotationPresent(RPC.class)) {
            throw new ProcessDefinitionException(
                String.format("The method %s is not annotated with @RPC.", rpcMethod.getName())
            );
        }

        final Class<?> contextClass;
        final Class<?> persistenceClass;
        final Class<?> communicationClass;

        final Class<?>[] parameterTypes = rpcMethod.getParameterTypes();

        if (RPC_METHOD_PARAMETERS_FULL_LENGTH == parameterTypes.length) {
            contextClass = parameterTypes[0];
            persistenceClass = parameterTypes[2];
            communicationClass = parameterTypes[3];
        } else if (RPC_METHOD_PARAMETERS_FULL_LENGTH - 1 == parameterTypes.length) {
            contextClass = parameterTypes[0];
            persistenceClass = parameterTypes[1];
            communicationClass = parameterTypes[2];
        } else {
            throw new ProcessDefinitionException(
                String.format("The @RPC method %s does not have a valid parameter list", rpcMethod.getName())
            );
        }

        if (
            !contextClass.equals(Context.class) ||
            !persistenceClass.equals(Persistence.class) ||
            !communicationClass.equals(Communication.class)
        ) {
            throw new ProcessDefinitionException(
                String.format("The @RPC method %s does not have a valid parameter list", rpcMethod.getName())
            );
        }
    }

    public static Object invoke(
        final Method rpcMethod,
        final Process process,
        final Context context,
        final Object input,
        final Persistence persistence,
        final Communication communication
    ) {
        final Class<?>[] parameterTypes = rpcMethod.getParameterTypes();

        Object output = null;

        try {
            if (RPC_METHOD_PARAMETERS_FULL_LENGTH == parameterTypes.length) {
                output = rpcMethod.invoke(process, context, input, persistence, communication);
            } else if (RPC_METHOD_PARAMETERS_FULL_LENGTH - 1 == parameterTypes.length) {
                output = rpcMethod.invoke(process, context, persistence, communication);
            }
        } catch (final Exception e) {
            throw new RpcException(
                String.format(
                    "Failed to invoke RPC %s in process %s",
                    rpcMethod.getName(),
                    ProcessUtil.getProcessType(process)
                ),
                e
            );
        }

        return output;
    }

    public static Class<?> getInputType(final Method rpcMethod) {
        final Class<?>[] parameterTypes = rpcMethod.getParameterTypes();

        if (RPC_METHOD_PARAMETERS_FULL_LENGTH == parameterTypes.length) {
            return parameterTypes[RPC_METHOD_PARAMETERS_INPUT_INDEX];
        } else if (RPC_METHOD_PARAMETERS_FULL_LENGTH - 1 == parameterTypes.length) {
            return null;
        } else {
            throw new ProcessDefinitionException(
                String.format("The @RPC method %s does not have a valid parameter list", rpcMethod.getName())
            );
        }
    }

    public static Object getInput(final Method rpcMethod, final Object[] args) {
        final Class<?>[] parameterTypes = rpcMethod.getParameterTypes();

        if (RPC_METHOD_PARAMETERS_FULL_LENGTH == parameterTypes.length) {
            return args[RPC_METHOD_PARAMETERS_INPUT_INDEX];
        } else if (RPC_METHOD_PARAMETERS_FULL_LENGTH - 1 == parameterTypes.length) {
            return null;
        } else {
            throw new ProcessDefinitionException(
                String.format("The @RPC method %s does not have a valid parameter list", rpcMethod.getName())
            );
        }
    }
}
