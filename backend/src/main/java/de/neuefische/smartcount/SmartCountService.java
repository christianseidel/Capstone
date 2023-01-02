package de.neuefische.smartcount;

import de.neuefische.smartcount.Exceptions.InvalidUserException;
import de.neuefische.smartcount.Users.User;
import de.neuefische.smartcount.Users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SmartCountService {

    private final ExpensesRepository expensesRepository;
    private final UserRepository userRepo;

    public Expense createExpense(Expense expense) {
        return expensesRepository.save(expense);
    }

    public Collection<Expense> getAllExpenses() {
        return expensesRepository.findAll();
    }

    public double getSum() {
        return Math.round(expensesRepository.findAll()
                .stream()
                .mapToDouble(a -> a.getAmount()).sum()*100)/100.0;
    }

    public Collection<Expense> getExpensesByUser(String user) {
        return expensesRepository.findAllByUser(user);
    }

    public double getSumByUser(String user) {
        return Math.round(expensesRepository.findAllByUser(user)
                .stream()
                .mapToDouble(a -> a.getAmount()).sum()*100)/100.0;
    }

    public void deleteExpense(String id, String user) {
        var item = expensesRepository.findByIdAndUser(id, user);
        if (item.isEmpty()) {
            throw new RuntimeException("An expense with Id no. " + id + " could not be found.");
        } else {
            expensesRepository.deleteById(id);
        }
    }

    public Optional<Expense> editExpense(String id, Expense expense, String user) {
        return expensesRepository.findByIdAndUser(id, user)
                .map(e -> expensesRepository.save(expense));
    }

    public Optional<Expense> getSingleExpense(String id, String user) {
        Optional<Expense> expense = expensesRepository.findByIdAndUser(id, user);
        if (!expense.isPresent()) {
            throw new InvalidUserException();
        }
        return expense;
    }

    public List<String> getUserList() {
        return userRepo.findAll().stream()
                .map(user -> user.getUsername()).toList();
    }


    //  -----   Who needs to pay how much at the end   -----   //

    public List<TransactionsDTO> amountPerPerson() {

        List<User> userList = userRepo.findAll();
        double arithMean = getArithMean(userList);

        List<UserDOO> usersWithExcess = new ArrayList<>();
        List<UserDOO> usersWithDeficit = new ArrayList<>();
        determinUserLists(userList, arithMean, usersWithExcess, usersWithDeficit);

        if (usersWithDeficit.size() == 0) {
            throw new RuntimeException("There is only one user in this group. No transaction will take place.");
        }

        // Make sure, all surpluses = all deficits
        double sumPluses = usersWithExcess.stream()
                .mapToDouble(UserDOO::userDelta)
                .sum();
        double sumMinuses = usersWithDeficit.stream()
                .mapToDouble(UserDOO::userDelta)
                .sum();

        // Any difference will be added to the account of the group member with the biggest stock of expenses.
        // This ensures that the while loop will produce no glitch.
        double newDelta = Math.round((usersWithExcess.get(0).userDelta() - (sumPluses-sumMinuses))*100)/100.0;
        UserDOO firstUser = usersWithExcess.get(0);
        usersWithExcess.set(0, firstUser.changeDelta(newDelta));

        int e = usersWithExcess.size()-1;
        int d = usersWithDeficit.size()-1;
        return computeTransactionsDTOS(usersWithExcess, usersWithDeficit, e, d);
    }

    private List<TransactionsDTO> computeTransactionsDTOS(List<UserDOO> usersWithExcess, List<UserDOO> usersWithDeficit, int excessIndex, int deficitIndex) {
        List<TransactionsDTO> listOfTransactions = new ArrayList<>();
        while (excessIndex >= 0) {
            UserDOO userWithExcess = usersWithExcess.get(excessIndex);
            UserDOO userWithDeficit = usersWithDeficit.get(deficitIndex);
            double exc = userWithExcess.userDelta();
            double defi = userWithDeficit.userDelta();
            if (exc == defi) {
                listOfTransactions.add(new TransactionsDTO(usersWithDeficit.get(deficitIndex).userName(), usersWithExcess.get(excessIndex).userName(), exc, Currency.EUR));  // The handling of different currencies is here being prepared for, but will only be implemented at a later point in time.
                excessIndex--;
                deficitIndex--;
            } else if (exc > defi) {
                listOfTransactions.add(new TransactionsDTO(usersWithDeficit.get(deficitIndex).userName(), usersWithExcess.get(excessIndex).userName(), defi, Currency.EUR));
                deficitIndex--;
                usersWithExcess.set(excessIndex, userWithExcess.changeDelta(Math.round((exc-defi)*100)/100.0));
            } else {
                listOfTransactions.add(new TransactionsDTO(usersWithDeficit.get(deficitIndex).userName(), usersWithExcess.get(excessIndex).userName(), exc, Currency.EUR));
                excessIndex--;
                usersWithDeficit.set(deficitIndex, userWithDeficit.changeDelta(Math.round((defi-exc)*100)/100.0));
            }
        }
        return listOfTransactions;
    }

    private double getArithMean(List<User> userList) {
        int numberOfPersons = userList.size();
        double arithMean = Math.round(getSum()/numberOfPersons*100)/100.0;
        if (arithMean==0.0) {
            throw new RuntimeException("There is no sum big enough to be divided.");
        }
        return arithMean;
    }

    private void determinUserLists(List<User> userList, double arithMean, List<UserDOO> usersWithExcess, List<UserDOO> usersWithDeficit) {
        for (User value : userList) {
            String user = value.getUsername();
            double sum = getSumByUser(user);
            if ((sum - arithMean) >= 0.0) {
                double delta = Math.round((sum - arithMean) * 100) / 100.0;
                var userRecord = new UserDOO(user, delta);
                usersWithExcess.add(userRecord);
            } else {
                double delta = Math.round((arithMean - sum) * 100) / 100.0;
                var userRecord = new UserDOO(user, delta);
                usersWithDeficit.add(userRecord);
            }
        }
        usersWithExcess.sort(Comparator.comparing(UserDOO::userDelta).reversed());
        usersWithDeficit.sort(Comparator.comparing(UserDOO::userDelta).reversed());
    }
}
