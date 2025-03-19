package matvey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping("/catering-service/dashboard")
    public String getPageOfDashboard() {
        return "dashboard";
    }
}
