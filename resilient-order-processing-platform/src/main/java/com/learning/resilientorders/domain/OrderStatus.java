package com.learning.resilientorders.domain;

public enum OrderStatus {
    RECEIVED,
    PENDING_INVENTORY,
    INVENTORY_RESERVED,
    COMPLETED,
    FAILED
}
