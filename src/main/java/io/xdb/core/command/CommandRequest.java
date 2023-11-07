package io.xdb.core.command;

import io.xdb.core.communication.Communication;
import io.xdb.core.persistence.Persistence;
import io.xdb.gen.models.CommandResults;
import io.xdb.gen.models.CommandWaitingType;
import io.xdb.gen.models.Context;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommandRequest {

    private final CommandWaitingType waitingType;
    private final List<BaseCommand> commands;

    /**
     * EMPTY command request will trigger {@link io.xdb.core.state.AsyncState#execute(Context, Object, CommandResults, Persistence, Communication)} immediately.
     */
    public static CommandRequest EMPTY = CommandRequest.builder().waitingType(CommandWaitingType.EMPTYCOMMAND).build();

    /**
     * To trigger {{@link io.xdb.core.state.AsyncState#execute(Context, Object, CommandResults, Persistence, Communication)}} after ANY of the commands has completed.
     *
     * @param commands  a list of commands to wait
     * @return
     */
    public static CommandRequest anyOfCommandsComplete(final BaseCommand... commands) {
        return CommandRequest
            .builder()
            .waitingType(CommandWaitingType.ANYOFCOMPLETION)
            .commands(Arrays.stream(commands).collect(Collectors.toList()))
            .build();
    }

    /**
     * To trigger {@link io.xdb.core.state.AsyncState#execute(Context, Object, CommandResults, Persistence, Communication)} after ALL the commands have completed.
     *
     * @param commands a list of commands to wait
     * @return
     */
    public static CommandRequest allOfCommandsComplete(final BaseCommand... commands) {
        return CommandRequest
            .builder()
            .waitingType(CommandWaitingType.ALLOFCOMPLETION)
            .commands(Arrays.stream(commands).collect(Collectors.toList()))
            .build();
    }
}
