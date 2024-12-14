package net.fhirfactory.dricats.platform.middleware.jgroups.valuesets;

public enum JGroupTransactionResultEnum {
    JGROUPS_TRANSACTION_RESULT_OK("OK", "JGroupsTransactionResult.OK", "JGroups Transaction Successful"),
    JGROUPS_TRANSACTION_RESULT_FAILED("FAILED", "JGroupsTransactionResult.Failed", "JGroups Transaction Failed"),
    JGROUPS_TRANSACTION_RESULT_UNKNOWN("UNKNOWN", "JGroupsTransactionResult.Unknown", "JGroups Transaction Unknown");

    private String code;
    private String description;
    private String longCode;

    private JGroupTransactionResultEnum(String transactionResultCode, String transactionResultLongCode, String transactionResultDescription) {
        this.code = transactionResultCode;
        this.description = transactionResultDescription;
        this.longCode = transactionResultLongCode;
    }

    public String getCode() {
        return code;
    }
    public String getDescription() {return description;}
    public String getLongCode() {return longCode;}
}
