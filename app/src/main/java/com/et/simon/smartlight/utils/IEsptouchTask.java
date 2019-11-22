package com.et.simon.smartlight.utils;

import java.util.List;

public interface IEsptouchTask {

	/**
	 * 设置 ESP-Touch 监听,当设备连接到AP时,它将被回调
	 * @param esptouchListener
	 */
	void setEsptouchListener(IEsptouchListener esptouchListener);

    /**
     * 当用户点击或关闭应用程序时，中断 ESP-Touch 任务
     */
	void interrupt();

    /**
     * 注意:!!!不要在 UI主线程 或 RuntimeException 中调用该任务
     * 抛出执行执行 ESP-Touch 任务异常并返回结果
     *
     * Smart Config v2.4 支持该API
     *
     * @return IEsptouchResult
     * @throws RuntimeException
     */
	IEsptouchResult executeForResult() throws RuntimeException;

    /**
     * 注意:!!!不要在 UI主线程 或 RuntimeException 中调用该任务
     * 抛出执行执行 ESP-Touch 任务异常并返回结果
     *
     * Smart Config v2.4 支持该API
     *
     * 直到客户端接收到结果计数值 count >= expectTaskResultCount 时.
     * 如果失败,则返回一个失败的结果到列表中
     * 如果执行过程中取消,
     *      接受到一些结果的,将结果返回到列表中
     *     没有收到任何结果,将在返回一个取消结果到列表中
     * @param expectTaskResultCount
     *              期待的结果计数值 expectTaskResultCount <= 0, expectTaskResultCount = Integer.MAX_VALUE
     * @return  返回 IEsptouchResult 到列表中
     * @throws RuntimeException
     */
	List<IEsptouchResult> executeForResults(int expectTaskResultCount) throws RuntimeException;
	
	/**
	 * 检查任务是否被用户取消
	 * 
	 * @return whether the task is cancelled by user
	 */
	boolean isCancelled();
}
