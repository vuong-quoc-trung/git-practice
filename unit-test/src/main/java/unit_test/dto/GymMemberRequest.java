package unit_test.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import unit_test.model.MembershipPlan;

public class GymMemberRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotNull(message = "Loại gói tập chỉ nhận BASIC hoặc PREMIUM")
    private MembershipPlan planType;

    public GymMemberRequest() {
    }

    public GymMemberRequest(String fullName, String email, String phone, MembershipPlan planType) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.planType = planType;
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
}
