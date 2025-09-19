package Hello.Sugang.exception;

import Hello.Sugang.exception.controller.ErrorController;
import Hello.Sugang.exception.controller.ErrorResult;
import Hello.Sugang.exception.controller.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = ErrorController.class)
public class exControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult illegalExhandle(IllegalArgumentException e){
        return new ErrorResult("BAD",e.getMessage());
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult userExHandle(UserException e){
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return errorResult;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResult exHandle(Exception e) {
        return new ErrorResult("EX", "내부 오류");
    }
}
