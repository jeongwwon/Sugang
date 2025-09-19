package Hello.Sugang.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/error")
@Slf4j
public class ErrorController {
    @GetMapping("/{id}")
    public void go(@PathVariable("id")Long id){
        if(id==10){
            throw new RuntimeException("잘못된 사용자");
        }else if(id==20){
            throw new IllegalArgumentException("잘못된 입력 값");
        }else if(id==30){
            throw new UserException("사용자 오류");
        }
    }
}
