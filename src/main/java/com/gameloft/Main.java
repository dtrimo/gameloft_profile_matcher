package com.gameloft;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

@EnableAutoConfiguration
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

}