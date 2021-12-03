package com.velvetalon.entity;

import lombok.Data;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/12/1 15:32 : 创建文件
 */
@Data
public class AutoRepeatEntity {
    String groupCode;
    String lastRepeat;
    String lastMsg;
}
