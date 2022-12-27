package com.leyou.trade.config;

import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.UserContext;
import org.springframework.stereotype.Component;

@Component
public class XXXX {

    public String getCollName() {

        UserDetail user = UserContext.getUser();

        if (user == null) {
            return "";
        }else{
            return "carts" + (user.getId() % 100);
        }
    }
}
