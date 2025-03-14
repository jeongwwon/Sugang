package Hello.Sugang.web;

import Hello.Sugang.web.SessionConst;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String go(HttpSession session){
        if (session!=null && session.getAttribute(SessionConst.LOGIN_MEMBER)!=null){
            return "redirect:/loginhome";
        }
        return "home";
    }
    @RequestMapping("/loginhome")
    public String loginHome() {
        return "loginhome"; //
    }
}
