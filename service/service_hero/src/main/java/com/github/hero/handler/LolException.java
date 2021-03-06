package com.github.hero.handler;

import com.github.hero.common.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常
 */
@Data
@AllArgsConstructor  //生成有参数构造方法
@NoArgsConstructor   //生成无参数构造
public class LolException extends RuntimeException {
    private ResultCode code;//状态码
    private String msg;//异常信息
}
