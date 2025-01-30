package com.github.farmplus.repository.party;

public enum PartyStatus {
    COMPLETED("완료"),
    RECRUITING("모집 중"),
    FAILED("실패");

    private final String statusInKorean;

    // 생성자
    PartyStatus(String statusInKorean) {
        this.statusInKorean = statusInKorean;
    }

    // 한글 상태 값을 반환하는 메서드
    public String getStatusInKorean() {
        return statusInKorean;
    }

}
