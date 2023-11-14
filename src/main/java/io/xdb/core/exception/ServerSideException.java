package io.xdb.core.exception;

import feign.FeignException;
import io.xdb.core.encoder.base.ObjectEncoder;

public class ServerSideException extends XDBHttpException {

    public ServerSideException(final ObjectEncoder objectEncoder, final FeignException.FeignClientException exception) {
        super(objectEncoder, exception);
    }
}
