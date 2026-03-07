package com.example.LLD.Splitwise;

import java.util.Arrays;

import com.example.LLD.Splitwise.controllers.BalanceSheetController;
import com.example.LLD.Splitwise.controllers.GroupController;
import com.example.LLD.Splitwise.expense.ExpenseSplitType;
import com.example.LLD.Splitwise.group.Group;
import com.example.LLD.Splitwise.split.Split;
import com.example.LLD.Splitwise.user.User;
import com.example.LLD.Splitwise.user.UserController;

public class Splitwise {
    private static Splitwise splitwiseInstance;
    private GroupController groupController;
    private BalanceSheetController balanceSheetController;
    private UserController userController;

    private Splitwise(){
        groupController = new GroupController();
        balanceSheetController = new BalanceSheetController();
        userController = new UserController();
    }

    public static Splitwise getInstance(){
        if(splitwiseInstance==null){
            synchronized(Splitwise.class){
                if(splitwiseInstance==null){
                    splitwiseInstance = new Splitwise();
                }
            }
        }
        return splitwiseInstance;
    }

    public void runSplitwiseDemo(){
        System.out.println("Starting Splitwise Demo...");
        setupUsersAndGroup();
        System.out.println("Users and group setup complete.");
        // Step 1: Add members to the group
        Group group = groupController.getGroup("G1001");
        group.addMember(userController.getUser("U2001")); // Bob
        group.addMember(userController.getUser("U3001")); // Charlie
         // Step 2: Create expenses within the group
        group.createExpense(
                "Exp1001", "Breakfast", 900,
                Arrays.asList(
                        new Split(300, userController.getUser("U1001")), // Alice
                        new Split(300, userController.getUser("U2001")), // Bob
                        new Split(300, userController.getUser("U3001"))  // Charlie
                ),
                ExpenseSplitType.EQUAL,
                userController.getUser("U1001") // Alice created the expense
        );

        group.createExpense(
                "Exp1002", "Lunch", 500,
                Arrays.asList(
                        new Split(400, userController.getUser("U1001")), // Alice
                        new Split(100, userController.getUser("U2001"))  // Bob
                ),
                ExpenseSplitType.UNEQUAL,
                userController.getUser("U2001") // Bob created the expense
        );

        // Display balance sheets
        for (User user : userController.getAllUsers()) {
            balanceSheetController.showBalanceSheetOfUser(user);
        }
    }

    private void setupUsersAndGroup() {
        registerUsers();

        // Create a group by Alice
        groupController.createGroup("G1001", "Outing with Friends", userController.getUser("U1001"));
    }
    

    private void registerUsers() {
        userController.addUser(new User("U1001", "Alice"));
        userController.addUser(new User("U2001", "Bob"));
        userController.addUser(new User("U3001", "Charlie"));
    }

}
