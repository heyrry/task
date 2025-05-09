package com.herry.task.repository.dataobject;

/**
 * @author herry
 * @since 2025/04/14
 */
public class TaskWithBLOBs extends TaskDO {

    /**
     * 上下文
     */
    private String context;

    /**
     * 结果
     */
    private String result;

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void setResult(String result) {
        this.result = result;
    }
}
