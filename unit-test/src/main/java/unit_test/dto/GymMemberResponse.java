package unit_test.dto;

import java.time.LocalDate;
import unit_test.model.MembershipPlan;

public class GymMemberResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private MembershipPlan planType;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private long monthlyFee;

    public GymMemberResponse() {
    }

    public GymMemberResponse(Long id, String fullName, String email, String phone, MembershipPlan planType, LocalDate startDate, LocalDate endDate, boolean active, long monthlyFee) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.planType = planType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.monthlyFee = monthlyFee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public MembershipPlan getPlanType() {
        return planType;
    }

    public void setPlanType(MembershipPlan planType) {
        this.planType = planType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(long monthlyFee) {
        this.monthlyFee = monthlyFee;
    }
}
