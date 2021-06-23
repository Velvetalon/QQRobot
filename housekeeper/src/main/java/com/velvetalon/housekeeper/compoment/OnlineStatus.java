package com.velvetalon.housekeeper.compoment;

/**
 * @describe: 在线状态
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/23 16:29 : 创建文件
 */
public enum OnlineStatus {
    // 目标在线
    ONLINE,
    // 目标离线，唤醒成功
    OFFLINE_RESTART_SUCCESS,
    // 目标离线，唤醒失败
    OFFLINE_RESTART_FAIL;
}
