package io.xdb.core.exception;

import feign.FeignException;
import io.xdb.core.encoder.ObjectEncoder;
import io.xdb.gen.models.ApiErrorResponse;
import io.xdb.gen.models.EncodedObject;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class XDBHttpException extends RuntimeException {

    private final int statusCode;
    private ApiErrorResponse apiErrorResponse;

    public XDBHttpException(final ObjectEncoder objectEncoder, final FeignException.FeignClientException exception) {
        super(exception);
        statusCode = exception.status();

        String decodeErrorMessage = "";
        final Optional<ByteBuffer> respBody = exception.responseBody();
        if (respBody.isPresent()) {
            final String data = StandardCharsets.UTF_8.decode(respBody.get()).toString();
            try {
                apiErrorResponse = objectEncoder.decode(new EncodedObject().data(data), ApiErrorResponse.class);
                return;
            } catch (final Exception e) {
                decodeErrorMessage = e.getMessage();
            }
        }

        // TODO
        apiErrorResponse =
            new ApiErrorResponse().detail("empty or unable to decode to apiErrorResponse: " + decodeErrorMessage);
    }

    public static XDBHttpException fromFeignException(
        final ObjectEncoder objectEncoder,
        final FeignException.FeignClientException exception
    ) {
        if (exception.status() >= 400 && exception.status() < 500) {
            return new ClientSideException(objectEncoder, exception);
        } else {
            return new ServerSideException(objectEncoder, exception);
        }
    }
}
