package unit_test.model;

public enum MembershipPlan {
    BASIC(300_000L),
    PREMIUM(500_000L);

    private final long monthlyFee;

    MembershipPlan(long monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public long getMonthlyFee() {
        return monthlyFee;
    }
}
