package integ.spring;

import static io.xdb.core.worker.WorkerService.API_PATH_ASYNC_STATE_EXECUTE;
import static io.xdb.core.worker.WorkerService.API_PATH_ASYNC_STATE_WAIT_UNTIL;
import static io.xdb.core.worker.WorkerService.API_PATH_PROCESS_RPC;

import io.xdb.core.worker.WorkerService;
import io.xdb.gen.models.AsyncStateExecuteRequest;
import io.xdb.gen.models.AsyncStateExecuteResponse;
import io.xdb.gen.models.AsyncStateWaitUntilRequest;
import io.xdb.gen.models.AsyncStateWaitUntilResponse;
import io.xdb.gen.models.ProcessRpcWorkerRequest;
import io.xdb.gen.models.ProcessRpcWorkerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class XdbWorkerApiController {

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
