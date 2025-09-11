package com.fire.fire_response_system.domain.dispatch;

public enum DispatchStatus {
    DRAFT(0),   // 작성중
    SENT(1),    // 발송됨
    ENDED(2);   // 종료됨

    private final int code;
    DispatchStatus(int code) { this.code = code; }
    public int getCode() { return code; }

    public static DispatchStatus from(Integer code) {
        if (code == null) return null;
        for (DispatchStatus s : values()) if (s.code == code) return s;
        throw new IllegalArgumentException("Unknown DispatchStatus code: " + code);
    }
}
