package br.com.tvalerio.exercice.utils;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class Utils {
    public String getDayOfWeek() {
        return LocalDate.now().getDayOfWeek().name().toUpperCase();
    }
}
