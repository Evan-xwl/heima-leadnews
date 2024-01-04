package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

/**
 * @author ruoling
 * @date 2024/1/4 16:07:12
 * @description
 */
public interface TaskService {

    long addTask(Task task);

    boolean cancelTask(long taskId);

    Task poll(int type,int priority);


}
