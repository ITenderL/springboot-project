package com.itender.mapstruct.mapper;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @author itender
 * @date 2022/9/22 17:47
 * @desc
 */
@Component
public class DateMapper {

    public String asString(Date date) {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(date) : null;
    }

    public Date asDate(String date) {
        try {
            return date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(date) : null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}