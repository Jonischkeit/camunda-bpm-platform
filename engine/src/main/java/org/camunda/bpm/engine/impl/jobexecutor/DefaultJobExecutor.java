/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.impl.jobexecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>This is a simple implementation of the {@link JobExecutor} using self-managed
 * threads for performing background work.</p>
 *
 * <p>This implementation uses a {@link ThreadPoolExecutor} backed by a queue to which
 * work is submitted.</p>
 *
 * <p><em>NOTE: use this class in environments in which self-management of threads
 * is permitted. Consider using a different thread-management strategy in
 * J(2)EE-Environments.</em></p>
 *
 * @author Daniel Meyer
 */
public class DefaultJobExecutor extends ThreadPoolJobExecutor {

  private static Logger log = Logger.getLogger(DefaultJobExecutor.class.getName());

  protected int queueSize = 3;
  protected int corePoolSize = 3;
  protected int maxPoolSize = 10;

  protected void startExecutingJobs() {

    if (threadPoolExecutor==null || threadPoolExecutor.isShutdown()) {
      BlockingQueue<Runnable> threadPoolQueue = new ArrayBlockingQueue<Runnable>(queueSize);
      threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, threadPoolQueue);
      threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
    }

    super.startExecutingJobs();
  }

  protected void stopExecutingJobs() {

    super.stopExecutingJobs();

    // Ask the thread pool to finish and exit
    threadPoolExecutor.shutdown();

    // Waits for 1 minute to finish all currently executing jobs
    try {
      if(!threadPoolExecutor.awaitTermination(60L, TimeUnit.SECONDS)) {
        log.log(Level.WARNING, "Timeout during shutdown of job executor. "
                + "The current running jobs could not end within 60 seconds after shutdown operation.");
      }
    } catch (InterruptedException e) {
      log.log(Level.WARNING, "Interrupted while shutting down the job executor. ", e);
    }
  }

  // getters and setters //////////////////////////////////////////////////////

  public int getQueueSize() {
    return queueSize;
  }

  public void setQueueSize(int queueSize) {
    this.queueSize = queueSize;
  }

  public int getCorePoolSize() {
    return corePoolSize;
  }

  public void setCorePoolSize(int corePoolSize) {
    this.corePoolSize = corePoolSize;
  }

  public int getMaxPoolSize() {
    return maxPoolSize;
  }

  public void setMaxPoolSize(int maxPoolSize) {
    this.maxPoolSize = maxPoolSize;
  }

}

