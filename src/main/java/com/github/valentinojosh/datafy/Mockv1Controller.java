package com.github.valentinojosh.datafy;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class Mockv1Controller {

//    @GetMapping("/topfive")
//    public Artist[] handleAuthCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
//
//        HttpSession session = request.getSession();
//        System.out.println("Top artists in get method:" + session.getAttribute("topfive"));
//        System.out.println("Test session var:" + session.getAttribute("test"));
//        return (Artist[]) session.getAttribute("topfive");
//    }

}