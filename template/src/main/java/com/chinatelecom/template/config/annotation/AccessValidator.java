package com.chinatelecom.template.config.annotation;

import javax.servlet.http.HttpServletRequest;

public interface AccessValidator {

    boolean loginValidator(HttpServletRequest request);

    boolean privilegeValidator(HttpServletRequest request);
}
