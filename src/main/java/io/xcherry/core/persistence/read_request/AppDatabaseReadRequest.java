package io.xcherry.core.persistence.read_request;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDatabaseReadRequest {

    private final List<AppDatabaseTableReadRequest> tableReadRequests;

    /**
     * Create an app database read request.
     *
     * @param tableReadRequests a list of {@link AppDatabaseTableReadRequest}.
     * @return  the created app database read request.
     */
    public static AppDatabaseReadRequest create(final AppDatabaseTableReadRequest... tableReadRequests) {
        return AppDatabaseReadRequest.create(Arrays.stream(tableReadRequests).collect(Collectors.toList()));
    }

    /**
     * Create an app database read request.
     *
     * @param tableReadRequests a list of {@link AppDatabaseTableReadRequest}.
     * @return  the created app database read request.
     */
    public static AppDatabaseReadRequest create(final List<AppDatabaseTableReadRequest> tableReadRequests) {
        return new AppDatabaseReadRequest(tableReadRequests);
    }

    public List<AppDatabaseTableReadRequest> getTableReadRequests() {
        return tableReadRequests;
    }

    public io.xcherry.gen.models.AppDatabaseReadRequest toApiModel() {
        return new io.xcherry.gen.models.AppDatabaseReadRequest()
            .tables(
                tableReadRequests.stream().map(AppDatabaseTableReadRequest::toApiModel).collect(Collectors.toList())
            );
    }
}
