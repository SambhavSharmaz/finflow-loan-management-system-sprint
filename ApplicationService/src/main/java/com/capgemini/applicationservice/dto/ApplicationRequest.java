package com.capgemini.applicationservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ApplicationRequest {

    @NotBlank(message = "Full name is required.")
    @Size(max = 100, message = "Full name must be at most 100 characters.")
    private String fullName;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits.")
    private String phone;

    @NotBlank(message = "Company name is required.")
    @Size(max = 100, message = "Company name must be at most 100 characters.")
    private String company;

    @NotNull(message = "Salary is required.")
    @Positive(message = "Salary must be greater than 0.")
    private Double salary;

    @NotNull(message = "Loan amount is required.")
    @Positive(message = "Loan amount must be greater than 0.")
    private Double amount;

    @NotNull(message = "Tenure is required.")
    @Min(value = 1, message = "Tenure must be at least 1 month.")
    private Integer tenure;

    @NotBlank(message = "Purpose is required.")
    @Size(max = 200, message = "Purpose must be at most 200 characters.")
    private String purpose;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
