package de.neuefische.smartcount;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
@CrossOrigin
@RequiredArgsConstructor
public class SmartCountController {

    private final SmartCountService smartCountService;

    @PostMapping
    public Expense createExpense(@RequestBody Expense expense) {
        return smartCountService.createExpense(expense);
    }

    @GetMapping
    public ExpenseDTO getAllExpensesPlusSum() {
        return smartCountService.getExpensesPlusSum();
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable String id) {
        smartCountService.deleteExpense(id);
    }

    @PutMapping("/{id}")
    public Expense editExpense(@PathVariable String id, @RequestBody Expense expense) {
        try {
            return smartCountService.editExpense(id, expense);
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "was?");
        }
    }

    @GetMapping("/{id}")
    public Optional<Expense> getSingleExpense(@PathVariable String id) {
        return smartCountService.getSingleExpense(id);
    }
}
