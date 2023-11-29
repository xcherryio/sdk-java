package integ.spring;

import static io.xcherry.core.worker.WorkerService.API_PATH_ASYNC_STATE_EXECUTE;
import static io.xcherry.core.worker.WorkerService.API_PATH_ASYNC_STATE_WAIT_UNTIL;
import static io.xcherry.core.worker.WorkerService.API_PATH_PROCESS_RPC;

import io.xcherry.core.worker.WorkerService;
import io.xcherry.core.worker.WorkerServiceResponseEntity;
import io.xcherry.gen.models.AsyncStateExecuteRequest;
import io.xcherry.gen.models.AsyncStateWaitUntilRequest;
import io.xcherry.gen.models.ProcessRpcWorkerRequest;
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
    public ResponseEntity<?> handleAsyncStateWaitUntil(final @RequestBody AsyncStateWaitUntilRequest request) {
        final WorkerServiceResponseEntity responseEntity = workerService.handleAsyncStateWaitUntil(request);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    @PostMapping(API_PATH_ASYNC_STATE_EXECUTE)
    public ResponseEntity<?> handleAsyncStateExecute(final @RequestBody AsyncStateExecuteRequest request) {
        final WorkerServiceResponseEntity responseEntity = workerService.handleAsyncStateExecute(request);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    @PostMapping(API_PATH_PROCESS_RPC)
    public ResponseEntity<?> handleProcessRpc(final @RequestBody ProcessRpcWorkerRequest request) {
        final WorkerServiceResponseEntity responseEntity = workerService.handleProcessRpc(request);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
}
