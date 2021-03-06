package com.acgist.snail.gui.main;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acgist.snail.system.config.SystemConfig;
import com.acgist.snail.system.context.SystemThreadContext;
import com.acgist.snail.utils.ThreadUtils;

/**
 * <p>任务列表刷新器</p>
 * 
 * @author acgist
 * @since 1.0.0
 */
public final class TaskDisplay {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskDisplay.class);
	
	private static final TaskDisplay INSTANCE = new TaskDisplay();

	/**
	 * <p>主窗口控制器</p>
	 */
	private MainController controller;
	/**
	 * <p>初始化锁</p>
	 */
	private final Object lock = new Object();
	
	private TaskDisplay() {
	}
	
	public static final TaskDisplay getInstance() {
		return INSTANCE;
	}
	
	/**
	 * <p>启动任务列表刷新定时器</p>
	 * 
	 * @param controller 主窗口控制器
	 */
	public void newTimer(MainController controller) {
		LOGGER.info("启动任务列表刷新定时器");
		synchronized (this) {
			if(this.controller == null) {
				this.controller = controller;
				SystemThreadContext.timer(
					0,
					SystemConfig.TASK_REFRESH_INTERVAL.toSeconds(),
					TimeUnit.SECONDS,
					() -> refreshTaskStatus()
				);
				synchronized (this.lock) {
					this.lock.notifyAll();
				}
			}
		}
	}

	/**
	 * <p>刷新任务数据</p>
	 */
	public void refreshTaskList() {
		MainController controller = INSTANCE.controller;
		while(controller == null) {
			synchronized (this.lock) {
				ThreadUtils.wait(this.lock, Duration.ofSeconds(Byte.MAX_VALUE));
			}
			controller = INSTANCE.controller;
		}
		controller.refreshTaskList();
	}
	
	/**
	 * <p>刷新任务状态</p>
	 */
	public void refreshTaskStatus() {
		MainController controller = INSTANCE.controller;
		while(controller == null) {
			synchronized (this.lock) {
				ThreadUtils.wait(this.lock, Duration.ofSeconds(Byte.MAX_VALUE));
			}
			controller = INSTANCE.controller;
		}
		controller.refreshTaskStatus();
	}

}
