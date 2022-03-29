package de.neuefische.smartcount;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/expenses")
@CrossOrigin
@RequiredArgsConstructor
public class SmartCountController {

    private final SmartCountService smartCountService;

    @PostMapping
    public Collection<Expense> createExpense(@RequestBody Expense expense) {
        smartCountService.createExpense(expense);
        return smartCountService.getExpenses();
    }

    @GetMapping
    public Collection<Expense> getAllExpenses() {
        return smartCountService.getExpenses();
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable String id) {
        smartCountService.deleteExpense(id);
    }


}
