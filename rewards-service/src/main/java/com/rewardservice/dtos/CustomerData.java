package com.rewardservice.dtos;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for Transaction.
 */
@Data
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class CustomerData {
    private Long customerId;
    private Integer totalRewardsPoints;
    private Integer monthDuration;
    private String monthName;

    public CustomerData() {
    }

    public CustomerData(Long customerId, Integer totalRewardsPoints, Integer monthDuration, String monthName) {
        this.customerId = customerId;
        this.totalRewardsPoints = totalRewardsPoints;
        this.monthDuration = monthDuration;
        this.monthName = monthName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getTotalRewardsPoints() {
        return totalRewardsPoints;
    }

    public void setTotalRewardsPoints(Integer totalRewardsPoints) {
        this.totalRewardsPoints = totalRewardsPoints;
    }

    public Integer getMonthDuration() {
        return monthDuration;
    }

    public void setMonthDuration(Integer monthDuration) {
        this.monthDuration = monthDuration;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }


}
