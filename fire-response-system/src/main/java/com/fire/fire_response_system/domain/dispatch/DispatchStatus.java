package com.fire.fire_response_system.domain.dispatch;

public enum DispatchStatus {
    DRAFT,   // 편성 중
    SENT,    // 출동 명령 전송
    ACTIVE,  // 현장에서 활동 중
    ENDED    // 종료
}
