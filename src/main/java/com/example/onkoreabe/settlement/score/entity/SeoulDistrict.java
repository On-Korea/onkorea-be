package com.example.onkoreabe.settlement.score.entity;

/**
 * 서울특별시 자치구
 */
public enum SeoulDistrict {
    GANGNAM("강남구", 37.5172, 127.0473),
    GANGDONG("강동구", 37.5301, 127.1238),
    GANGBUK("강북구", 37.6396, 127.0257),
    GANGSEO("강서구", 37.5509, 126.8495),
    GWANAK("관악구", 37.4784, 126.9516),
    GWANGJIN("광진구", 37.5384, 127.0822),
    GURO("구로구", 37.4954, 126.8874),
    GEUMCHEON("금천구", 37.4519, 126.9021),
    NOWON("노원구", 37.6542, 127.0568),
    DOBONG("도봉구", 37.6688, 127.0471),
    DONGDAEMUN("동대문구", 37.5744, 127.0396),
    DONGJAK("동작구", 37.5124, 126.9393),
    MAPO("마포구", 37.5663, 126.9019),
    SEODAEMUN("서대문구", 37.5791, 126.9368),
    SEOCHO("서초구", 37.4837, 127.0324),
    SEONGDONG("성동구", 37.5634, 127.0371),
    SEONGBUK("성북구", 37.5894, 127.0167),
    SONGPA("송파구", 37.5145, 127.1059),
    YANGCHEON("양천구", 37.5170, 126.8664),
    YEONGDEUNGPO("영등포구", 37.5264, 126.8962),
    YONGSAN("용산구", 37.5324, 126.9900),
    EUNPYEONG("은평구", 37.6027, 126.9291),
    JONGNO("종로구", 37.5730, 126.9794),
    JUNG("중구", 37.5636, 126.9977),
    JUNGNANG("중랑구", 37.6063, 127.0925);

    private final String displayName;
    private final double latitude;   // 위도
    private final double longitude;  // 경도

    SeoulDistrict(String displayName, double latitude, double longitude) {
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
