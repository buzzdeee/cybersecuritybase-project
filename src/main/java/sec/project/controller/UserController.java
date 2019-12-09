/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sec.project.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import sec.project.domain.User;
import sec.project.repository.UserRepository;
import sec.project.repository.SignupRepository;

@Controller
public class UserController {

   private DataSource dataSource;
   private JdbcTemplate jdbcTemplateObject;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SignupRepository signupRepository;
    
    @PostConstruct
    public void init() {
        // add some users
        System.out.println("Going to Add a bunch of users!!!");
        userRepository.save(new User("admin", "admin", 1));
        userRepository.save(new User("notanadmin", "notanadmin", 0));
        userRepository.save(new User("user1", "user1", 0));
    }

   public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
      this.jdbcTemplateObject = new JdbcTemplate(dataSource);     
   }
    
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loadLogin() {
        return "login";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String submitLogin(HttpServletResponse response, @RequestParam String username, @RequestParam String password, Model model) {
        String errormessage;
        Cookie cookie;
        User user = userRepository.findByUsername(username);
        if ( user == null) {
            errormessage = "username not found";
            model.addAttribute("errormessage", errormessage);
            return "login";
        }
        if (!user.getPassword().equals(password)) {
            errormessage = "wrong password for valid user";
            model.addAttribute("errormessage", errormessage);
            return "login";
        }
        if (user.getAdmin() == 1) {
            cookie = new Cookie("admincookie", "sure");
        } else {
            cookie = new Cookie("admincookie", "nope");
        }
        System.out.println(user);
        response.addCookie(cookie);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("subscribers", signupRepository.findAll());
        return "redirect:/users";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String loadUsers(@CookieValue(value = "admincookie", defaultValue = "nope") String admincookie, Model model) {
        System.out.println(userRepository.findAll());
        if (admincookie.equals("sure")) {
          model.addAttribute("users", userRepository.findAll());
        }
        model.addAttribute("subscribers", signupRepository.findAll());
        return "users";
    }
}
