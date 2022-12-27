package com.leyou.trade.service;

import java.util.Map;

public interface PayService {
    String generatePayUrl(Long id);

    void handleNotify(Map<String, String> paramMap);
}
