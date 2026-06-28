package com.example.expense_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.expense_tracker.dto.DashboardResponse;
import com.example.expense_tracker.dto.InsightsResponse;
import com.example.expense_tracker.dto.MonthlyReportResponse;
import com.example.expense_tracker.dto.YearlyResponse;
import com.example.expense_tracker.service.BudgetService;
import com.example.expense_tracker.service.DashboardService;
import com.example.expense_tracker.service.UserExpenseService;
import com.example.expense_tracker.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard APIs")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    @Autowired
    UserService userService;

    @Autowired
    UserExpenseService userExpenseService;

    @Autowired
    BudgetService budgetService;

    @Autowired
    DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {
        DashboardResponse response = dashboardService.getDashboard(authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/report/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(Authentication authentication,
            @RequestParam int month,
            @RequestParam int year) {
        MonthlyReportResponse response = dashboardService.monthlyReport(authentication.getName(), month, year);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/report/yearly")
    public ResponseEntity<YearlyResponse> getYearlyReport(Authentication authentication,
            @RequestParam int year) {
        YearlyResponse response = dashboardService.yearlyReport(authentication.getName(), year);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/insights")
    public ResponseEntity<InsightsResponse> getInsights(Authentication authentication) {
        InsightsResponse response = dashboardService.calculateInsights(authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
