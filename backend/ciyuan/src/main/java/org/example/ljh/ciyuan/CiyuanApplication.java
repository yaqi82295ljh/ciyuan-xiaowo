package org.example.ljh.ciyuan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** 次元小窝 · 启动类 */
@SpringBootApplication

@RestController

@RequestMapping("/index")

@MapperScan("org.example.ljh.ciyuan.mapper")
public class CiyuanApplication {

    public static void main(String[] args) {
        SpringApplication.run(CiyuanApplication.class, args);
    }

}
