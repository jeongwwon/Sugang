package Hello.Sugang.domain.login.service;

import Hello.Sugang.domain.student.entity.Student;
import Hello.Sugang.domain.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final StudentRepository studentRepository;

    public Student login(String email,String password){
        return studentRepository.findByEmail(email)
                .filter(s->s.getPassword().equals(password))
                .orElse(null);
    }
}
