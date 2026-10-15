package com.codegym.controller;

import com.codegym.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("user")
public class LoginController {

    @ModelAttribute("user")
    public User setUpUserForm() {
        return new User();
    }

    @GetMapping({"/", "/login"})
    public String index(@CookieValue(value = "setUser", defaultValue = "") String setUser, Model model) {
        Cookie cookie = new Cookie("setUser", setUser);
        model.addAttribute("cookieValue", cookie);
        model.addAttribute("savedUser", setUser);

        User user = new User();
        if (setUser != null && !setUser.isEmpty()) {
            user.setEmail(setUser);
        }
        model.addAttribute("user", user);

        return "login";
    }

    @PostMapping({"/dologin", "/doLogin"})
    public String doLogin(@ModelAttribute("user") User user,
                          Model model,
                          @CookieValue(value = "setUser", defaultValue = "") String setUser,
                          HttpServletResponse response,
                          HttpServletRequest request) {

        // Kiểm tra tài khoản mẫu: admin@gmail.com / 12345
        if ("admin@gmail.com".equalsIgnoreCase(user.getEmail()) && "12345".equals(user.getPassword())) {
            setUser = user.getEmail();

            // Tạo Cookie và lưu trong 24 giờ
            Cookie cookie = new Cookie("setUser", setUser);
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);

            // Đọc lại Cookie từ request nếu có
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie ck : cookies) {
                    if ("setUser".equals(ck.getName())) {
                        model.addAttribute("cookieValue", ck);
                        break;
                    }
                }
            }
            if (!model.containsAttribute("cookieValue")) {
                model.addAttribute("cookieValue", cookie);
            }
            model.addAttribute("savedUser", setUser);

            model.addAttribute("message", "Đăng nhập thành công! Chào mừng " + user.getEmail() + ".");
            model.addAttribute("isSuccess", true);
        } else {
            user.setEmail("");
            Cookie cookie = new Cookie("setUser", setUser);
            model.addAttribute("cookieValue", cookie);
            model.addAttribute("savedUser", setUser);
            model.addAttribute("message", "Đăng nhập thất bại. Vui lòng kiểm tra lại email hoặc mật khẩu!");
            model.addAttribute("isSuccess", false);
        }

        return "login";
    }

    @GetMapping("/clear-cookie")
    public String clearCookie(HttpServletResponse response, Model model) {
        Cookie cookie = new Cookie("setUser", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        model.addAttribute("cookieValue", cookie);
        model.addAttribute("savedUser", "");
        model.addAttribute("user", new User());
        model.addAttribute("message", "Đã xóa Cookie ghi nhớ tên đăng nhập thành công.");
        model.addAttribute("isSuccess", true);

        return "login";
    }
}
