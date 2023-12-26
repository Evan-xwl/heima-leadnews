package com.heima.controller;

import com.heima.vo.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author ruoling
 * @date 2023/12/26 13:56:24
 * @description
 */
@Controller
public class BasicController {

    @GetMapping("/test1")
    public String freeMarker01(Model model){
        model.addAttribute("name", "张三");
        Student student = new Student();
        student.setAge(18);
        student.setName("李四");
        model.addAttribute("stu", student);
        return "basic";
    }
}
