package io.xcherry.core;

import static feign.FeignException.errorStatus;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

public class ServerErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(final String methodKey, final Response response) {
        if (response.status() >= 500 && response.status() < 600) {
            final FeignException exception = errorStatus(methodKey, response);
            return new RetryableException(
                response.status(),
                exception.getMessage(),
                response.request().httpMethod(),
                exception,
                null,
                response.request()
            );
        }

        return new ErrorDecoder.Default().decode(methodKey, response);
    }
}
