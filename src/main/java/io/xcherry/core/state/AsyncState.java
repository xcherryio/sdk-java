package io.xcherry.core.state;

import io.xcherry.core.command.CommandRequest;
import io.xcherry.core.command.CommandResults;
import io.xcherry.core.communication.Communication;
import io.xcherry.core.context.Context;
import io.xcherry.core.persistence.Persistence;
import java.lang.reflect.Method;

public interface AsyncState<I> {
    /**
     * {@link AsyncState#getInputType} is needed for deserializing data back into Java object.
     *
     * @return the type of the state input
     */
    Class<I> getInputType();

    /**
     *
     * @return the state options
     */
    default AsyncStateOptions getOptions() {
        return AsyncStateOptions.builder(this.getClass()).build();
    }

    /**
     * {@link AsyncState#waitUntil} is used to configure commands to wait for before invoking the {@link AsyncState#execute} API.
     * It's optional -- you have the option to skip overriding it in a subclass, in which case the {@link AsyncState#execute} API will be invoked directly instead.
     *
     * @param context
     * @param input
     * @param communication
     * @return
     */
    default CommandRequest waitUntil(final Context context, final I input, final Communication communication) {
        throw new IllegalStateException("this exception will never be thrown.");
    }

    /**
     * {@link AsyncState#execute} is used to perform an action and determine the next steps to take.
     * It's called after the commands specified in {@link AsyncState#waitUntil} have been completed or, in the case where {@link AsyncState#waitUntil} is skipped, it is invoked directly.
     *
     * @param context
     * @param input
     * @param commandResults
     * @param persistence
     * @param communication
     * @return
     */
    StateDecision execute(
        final Context context,
        final I input,
        final CommandResults commandResults,
        final Persistence persistence,
        final Communication communication
    );

    static boolean shouldSkipWaitUntil(final AsyncState state) {
        final Class<? extends AsyncState> stateClass = state.getClass();

        final Method waitUntilMethod;
        try {
            waitUntilMethod = stateClass.getMethod("waitUntil", Context.class, Object.class, Communication.class);
        } catch (final NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }

        if (waitUntilMethod.getDeclaringClass().equals(AsyncState.class)) {
            return true;
        }

        return false;
    }
}
