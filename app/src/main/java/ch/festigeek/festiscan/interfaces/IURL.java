package ch.festigeek.festiscan.interfaces;

public interface IURL {
    String BASE_URL = "http://api.festigeek.lan/v1/";
    //String BASE_URL = "http://192.168.2.123/v1/";

    String QR_DECRYPT = "qrcode/decrypt";
    String ALL_USERS = "users";
    String USER_BY_ID = "users/";

    String USER_ORDERS = "orders?userId=";

    String CHECK_IN = "abstract_products/"; // inscription/{id} PATCH

    String ORDERS = "orders/";
    String CONSUME = "/productConsumption";

    String PRODUCT = "";
}
