package com.fire.fire_response_system.domain.dispatch;

public enum DispatchStatus {

    DRAFT(0),
    SENT(1),
    ACTIVE(2),
    ENDED(3);

    private final int code;

    DispatchStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /** statusCode(int) → DispatchStatus 변환 */
    public static DispatchStatus from(Integer code) {
        if (code == null) return null;
        for (DispatchStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Invalid dispatch status code: " + code);
    }
}
