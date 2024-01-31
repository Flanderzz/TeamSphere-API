package com.YipYapTimeAPI.YipYapTimeAPI.config;

import org.springframework.beans.factory.annotation.Value;

public class JWTTokenConst {
    @Value("${env.JWT_KEY}")
    public static final String JWT_KEY = "kzjjbeiurwasdwadsawdsbZGyurZvzpaqekmeecfeeljsdwasdwaliuogcadwwsadwaderwmqzsduphbeheb";

    @Value("${env.JWT_AUTH}")
    public static final String HEADER = "Authorization";
}
