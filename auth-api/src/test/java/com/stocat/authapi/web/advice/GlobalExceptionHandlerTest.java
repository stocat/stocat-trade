package com.stocat.authapi.web.advice;

import com.stocat.authapi.exception.AuthErrorCode;
import com.stocat.common.exception.ApiException;
import com.stocat.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void ApiException은_400과_정의된_코드를_반환한다() {
        ApiException ex = new ApiException(AuthErrorCode.INVALID_CREDENTIALS);

        ResponseEntity<ApiResponse<?>> response = handler.handleApiException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(AuthErrorCode.INVALID_CREDENTIALS.code());
    }

    @Test
    void BeanValidation오류는_필드에러정보를_담아서_응답한다() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DummyDto(), "dummy");
        bindingResult.addError(new FieldError("dummy", "email", "이메일은 필수입니다."));

        Method method = DummyController.class.getDeclaredMethod("dummy", DummyDto.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ApiResponse<?>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(AuthErrorCode.INVALID_REQUEST.code());
        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) response.getBody().data();
        assertThat(details).isNotNull();
        List<?> fieldErrors = (List<?>) details.get("fieldErrors");
        assertThat(fieldErrors).hasSize(1);
    }

    @Test
    void 예기치못한_예외는_500으로_전달한다() {
        ResponseEntity<ApiResponse<?>> response = handler.handleUnexpected(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(AuthErrorCode.INTERNAL_ERROR.code());
        assertThat(response.getBody().message()).isEqualTo("boom");
    }

    private static class DummyController {
        public void dummy(DummyDto dto) {
        }
    }

    private record DummyDto() {
    }
}
