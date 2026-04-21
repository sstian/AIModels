package com.snow.service;

public interface FunctionAssistant {
    //客户指令：出差住宿发票开票，
    // 开票信息:    公司名称xxx
    // 税号序列:    xx
    // 开票金额:    xxx.00元
    String chat(String message);
}
