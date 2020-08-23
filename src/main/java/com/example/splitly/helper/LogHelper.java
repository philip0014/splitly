package com.example.splitly.helper;

import com.example.splitly.exception.BaseErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;

@Slf4j
public class LogHelper {

    public static void error(String message, Object ...objects) {
        if (!CollectionUtils.isEmpty(Arrays.asList(objects))
            && objects[objects.length - 1] instanceof BaseErrorException) {
            BaseErrorException t = (BaseErrorException) objects[objects.length - 1];
            if (!t.getHttpStatus().is4xxClientError()) {
                log.error(message, objects);
            }
        }
        else {
            log.error(message, objects);
        }

    }

    public static void info(String message, Object ...objects) {
        if (!CollectionUtils.isEmpty(Arrays.asList(objects))
            && objects[objects.length - 1] instanceof BaseErrorException) {
            BaseErrorException t = (BaseErrorException) objects[objects.length - 1];
            if (!t.getHttpStatus().is4xxClientError()) {
                log.info(message, objects);
            }
        } else {
            log.info(message, objects);
        }
    }

}
