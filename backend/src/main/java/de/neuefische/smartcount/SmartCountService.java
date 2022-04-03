package de.neuefische.smartcount;

import lombok.RequiredArgsConstructor;
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
        return expensesRepository.findAll();
    }

    public double getSum() {
        return expensesRepository.findAll()
                .stream()
                .mapToDouble(a -> a.getAmount()).sum();
    }

    public void deleteExpense(String id) {
        expensesRepository.deleteById(id);
    }

    public Expense editExpense(String id, Expense expense) {

        // Vorschlag von André:
        // => return expensesRepository.findById(id)
        // =>        .map(expense -> expensesRepository.save(expense));
        //
        // That way you can use the ResponseEntity.of-method inside the controller.
        // It returns a 404 if the Optional is empty and 200 if not.
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
