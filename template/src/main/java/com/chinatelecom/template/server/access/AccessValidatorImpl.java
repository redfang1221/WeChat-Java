package com.chinatelecom.template.server.access;

import com.chinatelecom.template.config.annotation.AccessValidator;
import com.chinatelecom.template.server.entity.Constants;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AccessValidatorImpl implements AccessValidator {

    @Override
    public boolean loginValidator(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(Constants.SESSION_SYS_USER);
        if (obj != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean privilegeValidator(HttpServletRequest request) {
        return true;
    }
}
