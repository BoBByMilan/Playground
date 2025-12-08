package net.sqlite;

public record TransactionContext(
        int transactions_Id,
        double amount,
        int merchantRiskLevel,
        String merchantCategory,
        double userAvgSpending,
        double userTotalSpending,
        int userTotalTransactions
) {
}
