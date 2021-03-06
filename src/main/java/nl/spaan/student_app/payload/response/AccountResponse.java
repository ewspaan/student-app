package nl.spaan.student_app.payload.response;

public class AccountResponse {

    private String accountNumber;
    private double waterUtility;
    private double gasUtility;
    private double elektraUtility;
    private double internetUtility;

    public AccountResponse() {
    }

    public AccountResponse(String accountNumber, double waterUtility, double gasUtility, double elektraUtility, double internetUtility) {
        this.accountNumber = accountNumber;
        this.waterUtility = waterUtility;
        this.gasUtility = gasUtility;
        this.elektraUtility = elektraUtility;
        this.internetUtility = internetUtility;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getWaterUtility() {
        return waterUtility;
    }

    public void setWaterUtility(double waterUtility) {
        this.waterUtility = waterUtility;
    }

    public double getGasUtility() {
        return gasUtility;
    }

    public void setGasUtility(double gasUtility) {
        this.gasUtility = gasUtility;
    }

    public double getElektraUtility() {
        return elektraUtility;
    }

    public void setElektraUtility(double elektraUtility) {
        this.elektraUtility = elektraUtility;
    }

    public double getInternetUtility() {
        return internetUtility;
    }

    public void setInternetUtility(double internetUtility) {
        this.internetUtility = internetUtility;
    }
}
