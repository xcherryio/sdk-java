package integ.spring;

import static io.xcherry.core.worker.WorkerService.API_PATH_ASYNC_STATE_EXECUTE;
import static io.xcherry.core.worker.WorkerService.API_PATH_ASYNC_STATE_WAIT_UNTIL;
import static io.xcherry.core.worker.WorkerService.API_PATH_PROCESS_RPC;

import io.xcherry.core.worker.WorkerService;
import io.xcherry.gen.models.AsyncStateExecuteRequest;
import io.xcherry.gen.models.AsyncStateExecuteResponse;
import io.xcherry.gen.models.AsyncStateWaitUntilRequest;
import io.xcherry.gen.models.AsyncStateWaitUntilResponse;
import io.xcherry.gen.models.ProcessRpcWorkerRequest;
import io.xcherry.gen.models.ProcessRpcWorkerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkerApiController {

    private final WorkerService workerService;

    @PostMapping(API_PATH_ASYNC_STATE_WAIT_UNTIL)
    public ResponseEntity<AsyncStateWaitUntilResponse> handleAsyncStateWaitUntil(
        final @RequestBody AsyncStateWaitUntilRequest request
    ) {
        return ResponseEntity.ok(workerService.handleAsyncStateWaitUntil(request));
    }

    @PostMapping(API_PATH_ASYNC_STATE_EXECUTE)
    public ResponseEntity<AsyncStateExecuteResponse> handleAsyncStateExecute(
        final @RequestBody AsyncStateExecuteRequest request
    ) {
        return ResponseEntity.ok(workerService.handleAsyncStateExecute(request));
    }

    @PostMapping(API_PATH_PROCESS_RPC)
    public ResponseEntity<ProcessRpcWorkerResponse> handleProcessRpc(
        final @RequestBody ProcessRpcWorkerRequest request
    ) {
        return ResponseEntity.ok(workerService.handleProcessRpc(request));
    }
}
