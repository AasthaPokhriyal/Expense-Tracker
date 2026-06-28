package com.example.expense_tracker.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.expense_tracker.Entity.UserExpenseEntity;

@Repository
public interface UserExpenseRepo extends JpaRepository<UserExpenseEntity, String> {
        public List<UserExpenseEntity> findAll();

        public UserExpenseEntity saveAndFlush(UserExpenseEntity newEntity);

        public Optional<UserExpenseEntity> findById(String expenseId);

        public void deleteById(String expenseId);

        public List<UserExpenseEntity> findByUserId(String userId);

        Optional<UserExpenseEntity> findByExpenseIdAndUserId(String expenseId, String userId);

        @Query("""
                        select e from Expenses e where e.userId=:userId AND MONTH(e.date)=:month AND YEAR(e.date)=:year
                        """)
        List<UserExpenseEntity> findByIdMonthAndYear(String userId, int month, int year);

        @Query("""
                        SELECT e
                        FROM Expenses e
                        WHERE e.userId = :userId
                        AND YEAR(e.date) = :year
                        """)
        List<UserExpenseEntity> findByUserIdYear(
                        @Param("userId") String userId,
                        @Param("year") int year);
}
