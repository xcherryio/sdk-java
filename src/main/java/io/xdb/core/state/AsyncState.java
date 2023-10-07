package io.xdb.core.state;

import io.xdb.gen.models.CommandRequest;
import java.lang.reflect.Method;
import lombok.NonNull;

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
    @NonNull
    default AsyncStateOptions getOptions() {
        return AsyncStateOptions.builder().build();
    }

    /**
     * {@link AsyncState#waitUntil} is used to configure commands to wait for before invoking the {@link AsyncState#execute} API.
     * It's optional -- you have the option to skip overriding it in a subclass, in which case the {@link AsyncState#execute} API will be invoked directly instead.
     *
     * @param input
     * @return
     */
    default CommandRequest waitUntil(final I input) {
        throw new IllegalStateException("this exception will never be thrown.");
    }

    /**
     * {@link AsyncState#execute} is used to perform an action and determine the next steps to take.
     * It's called after the commands specified in {@link AsyncState#waitUntil} have been completed or, in the case where {@link AsyncState#waitUntil} is skipped, it is invoked directly.
     *
     * @param input
     * @return
     */
    StateDecision execute(final I input);

    static boolean shouldSkipWaitUntil(final AsyncState state) {
        final Class<? extends AsyncState> stateClass = state.getClass();

        final Method waitUntilMethod;
        try {
            waitUntilMethod = stateClass.getMethod("waitUntil", Object.class);
        } catch (final NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }

        if (waitUntilMethod.getDeclaringClass().equals(AsyncState.class)) {
            return true;
        }

        return false;
    }
}
