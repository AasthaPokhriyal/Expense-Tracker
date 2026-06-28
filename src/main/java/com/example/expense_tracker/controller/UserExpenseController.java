package com.example.expense_tracker.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.expense_tracker.Entity.UserEntity;
import com.example.expense_tracker.Entity.UserExpenseEntity;
import com.example.expense_tracker.dto.ExpenseInput;
import com.example.expense_tracker.service.UserExpenseService;
import com.example.expense_tracker.service.UserService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Expense APIs")
@SecurityRequirement(name = "bearerAuth")
@RestController
public class UserExpenseController {

    @Autowired
    private UserExpenseService userExpenseService;

    @Autowired
    private UserService userService;

    @GetMapping("/fetch-all-expenses")
    public ResponseEntity<?> fetchAllExpenses() {
        return new ResponseEntity<>(userExpenseService.getAllExpenses(), HttpStatus.OK);
    }

    @GetMapping("view-expense")
    public ResponseEntity<?> viewExpenseById(Authentication authentication) {
        String username = authentication.getName();
        Optional<UserEntity> userOpt = userService.findByUsername(username);
        if (!userOpt.isPresent()) {
            return new ResponseEntity<>("No such user found", HttpStatus.NOT_FOUND);
        }
        UserEntity user = userOpt.get();
        String userId = user.getUserId();

        List<UserExpenseEntity> userList = userExpenseService.findByUserId(userId);
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @PostMapping("/add-expense")
    public ResponseEntity<?> addExpense(Authentication authentication, @Valid @RequestBody ExpenseInput expenseInput) {

        UserExpenseEntity savedExpense = userExpenseService.addNewExpense(authentication, expenseInput);

        return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    @PutMapping("/update-expense/{id}")
    public ResponseEntity<?> updateExpense(@Valid @RequestBody ExpenseInput updatedExpense,
            @PathVariable String expenseId) {
        UserExpenseEntity savedExpense = userExpenseService.addExpense(expenseId, updatedExpense);
        if (savedExpense == null)
            return new ResponseEntity<>("No such expense found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-expense/{expenseId}")
    public ResponseEntity<?> deleteExpense(
            Authentication authentication,
            @PathVariable String expenseId) {

        String username = authentication.getName();

        Optional<UserEntity> userOpt = userService.findByUsername(username);

        if (userOpt.isPresent()) {

            String userId = userOpt.get().getUserId();

            Optional<UserExpenseEntity> expenseOpt = userExpenseService.findByExpenseIdAndUserId(
                    expenseId,
                    userId);

            if (expenseOpt.isPresent()) {

                userExpenseService.deleteExpense(expenseOpt.get().getExpenseId());

                return ResponseEntity.ok(
                        "Expense deleted successfully");
            }
        }

        return ResponseEntity.status(
                HttpStatus.NOT_FOUND).build();
    }
}
