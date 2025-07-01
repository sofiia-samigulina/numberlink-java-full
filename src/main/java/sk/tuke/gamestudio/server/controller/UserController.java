package sk.tuke.gamestudio.server.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.tuke.gamestudio.entity.Users;
import sk.tuke.gamestudio.service.UsersService;

@Controller
@RequestMapping("/numberlink")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserController {

    @Autowired
    UsersService usersService;

    public UserController() {

    }

    private boolean containsSameName(String user) {
        for (Users u: usersService.getUsers()) {
            if (u.getPlayer().equals(user)) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/login")
    public String login(@RequestParam(required = false)String username, @RequestParam(required = false)String password, RedirectAttributes redirectAttributes, HttpSession session) {
        if (username != null && password != null) {
            Users user = new Users(username, password);
            if (containsSameName(username) && usersService.getPassword(user.getPlayer()).equals(password)) {
                session.setAttribute("loggedUser", username);
                return "redirect:/numberlink";
            }
            else if (!containsSameName(username)) {
                redirectAttributes.addFlashAttribute("error", "You should be registrated");
                return "redirect:/numberlink/login";
            }
            else if (!usersService.getPassword(user.getPlayer()).equals(password) && containsSameName(username)) {
                redirectAttributes.addFlashAttribute("error", "Incorrect username or password");
                return "redirect:/numberlink/login";
            }

        }
        return "login";
    }

    @RequestMapping("/registration")
    public String registration(@RequestParam(required = false)String username, @RequestParam(required = false)String password, RedirectAttributes redirectAttributes) {
        if (username != null && password != null) {
            if (containsSameName(username) && !usersService.getPassword(username).equals(password)) {
                redirectAttributes.addFlashAttribute("error", "That username isn't available");
                return "redirect:/numberlink/registration";
            }
            else if (containsSameName(username) && usersService.getPassword(username).equals(password)) {
                redirectAttributes.addFlashAttribute("error", "You are already registered");
                return "redirect:/numberlink/registration";
            }
            else {
                Users user = new Users(username, password);
                usersService.addUser(user);
                return "redirect:/numberlink/login";
            }
        }
        return "signup";
    }
}
