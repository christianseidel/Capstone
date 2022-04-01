package de.neuefische.smartcount;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SmartCountService {

    private final ExpensesRepository expensesRepository;

    public Expense createExpense(Expense expense) {
        return expensesRepository.save(expense);
    }

    public Collection<Expense> getExpenses() {
        return expensesRepository.findAll()
                .stream()
                .toList();
    }

    private double getSum() {
        return expensesRepository.findAll()
                .stream()
                .mapToDouble(a -> a.getAmount()).sum();
    }

    public ExpenseDTO getExpensesPlusSum() {
        return new ExpenseDTO(getExpenses(), getSum());
    }

    public void deleteExpense(String id) {
        expensesRepository.deleteById(id);
    }

    public Expense editExpense(String id, Expense expense) {
        var item = expensesRepository.findById(id);
        if (item.isEmpty()) {
            throw new RuntimeException("Diese Id ist nicht bekannt!");
        } else {
            return expensesRepository.save(expense);
        }
    }

    public Optional<Expense> getSingleExpense(String id) {
        return expensesRepository.findById(id);
    }
}
