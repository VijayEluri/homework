package nachos.threads;

import nachos.machine.Lib;
import nachos.machine.Machine;
import java.util.*;
import java.util.Map.Entry;

/**
 * A scheduler that chooses threads based on their priorities.
 * 
 * <p>
 * A priority scheduler associates a priority with each thread. The next thread
 * to be dequeued is always a thread with priority no less than any other
 * waiting thread's priority. Like a round-robin scheduler, the thread that is
 * dequeued is, among all the threads of the same (highest) priority, the thread
 * that has been waiting longest.
 * 
 * <p>
 * Essentially, a priority scheduler gives access in a round-robin fassion to
 * all the highest-priority threads, and ignores all other threads. This has the
 * potential to starve a thread if there's always a thread waiting with higher
 * priority.
 * 
 * <p>
 * A priority scheduler must partially solve the priority inversion problem; in
 * particular, priority must be donated through locks, and through joins.
 */
public class PriorityScheduler extends Scheduler {
	/**
	 * Allocate a new priority scheduler.
	 */
	public PriorityScheduler() {
	}

	/**
	 * Allocate a new priority thread queue.
	 * 
	 * @param transferPriority
	 *            <tt>true</tt> if this queue should transfer priority from
	 *            waiting threads to the owning thread.
	 * @return a new priority thread queue.
	 */
	public ThreadQueue newThreadQueue(boolean transferPriority) {
		return new PriorityQueue(transferPriority);
	}

	public int getPriority(KThread thread) {
		Lib.assertTrue(Machine.interrupt().disabled());

		return getThreadState(thread).getPriority();
	}

	public int getEffectivePriority(KThread thread) {
		Lib.assertTrue(Machine.interrupt().disabled());

		return getThreadState(thread).getEffectivePriority();
	}

	public void setPriority(KThread thread, int priority) {
		Lib.assertTrue(Machine.interrupt().disabled());

		Lib.assertTrue(priority >= priorityMinimum
				&& priority <= priorityMaximum);

		getThreadState(thread).setPriority(priority);
	}

	public boolean increasePriority()
	{
		boolean intStatus = Machine.interrupt().disable();

		KThread thread = KThread.currentThread();

		int priority = getPriority(thread);
		if (priority == priorityMaximum)
		{
		  Machine.interrupt().restore(intStatus); // bug identified by Xiao Jia @ 2011-11-04
			return false;
		}

		setPriority(thread, priority + 1);

		Machine.interrupt().restore(intStatus);
		return true;
	}

	public boolean decreasePriority()
	{
		boolean intStatus = Machine.interrupt().disable();

		KThread thread = KThread.currentThread();

		int priority = getPriority(thread);
		if (priority == priorityMinimum)
		{
		  Machine.interrupt().restore(intStatus); // bug identified by Xiao Jia @ 2011-11-04
			return false;
		}

		setPriority(thread, priority - 1);

		Machine.interrupt().restore(intStatus);
		return true;
	}

	/**
	 * The default priority for a new thread. Do not change this value.
	 */
	public static final int priorityDefault = 1;
	/**
	 * The minimum priority that a thread can have. Do not change this value.
	 */
	public static final int priorityMinimum = 0;
	/**
	 * The maximum priority that a thread can have. Do not change this value.
	 */
	public static final int priorityMaximum = 7;

	/**
	 * Return the scheduling state of the specified thread.
	 * 
	 * @param thread
	 *            the thread whose scheduling state to return.
	 * @return the scheduling state of the specified thread.
	 */
	protected ThreadState getThreadState(KThread thread) {
		if (thread.schedulingState == null)
			thread.schedulingState = new ThreadState(thread);

		return (ThreadState) thread.schedulingState;
	}

	/**
	 * A <tt>ThreadQueue</tt> that sorts threads by priority.
	 */
	protected class PriorityQueue extends ThreadQueue {
		PriorityQueue(boolean transferPriority) {
			this.transferPriority = transferPriority;
			threadQueue = new TreeMap<Integer, TreeSet<ThreadState>>();
			threadPool = new TreeMap<ThreadState, Integer>();
		}
		
		protected void acquire(int priority, ThreadState state) {
			if (threadQueue.containsKey(priority)) {
				threadQueue.get(priority).add(state);
			} else {
				TreeSet<ThreadState> set = new TreeSet<ThreadState>();
				set.add(state);
				threadQueue.put(priority, set);
			}
		}
		
		protected void remove(int priority, ThreadState state) {
			if (!threadQueue.containsKey(priority)) return;
			TreeSet<ThreadState> set = threadQueue.get(priority);
			set.remove(state);
			if (set.isEmpty()) threadQueue.remove(priority);
		}

		public void waitForAccess(KThread thread) {
			Lib.assertTrue(Machine.interrupt().disabled());
			//getThreadState(thread).waitForAccess(this);
			
			ThreadState state = getThreadState(thread);
			state.incomingTime = Machine.timer().getTime();
			int priority = state.getEffectivePriority();
			acquire(priority, state);
			if (priority > donatingPriority) {
				donatingPriority = priority;
				if (transferPriority && owner != null)
					promoteState(getThreadState(owner));
			}
			threadPool.put(state, priority);
			state.belongingQueue = this;
		}

		public void acquire(KThread thread) {
			Lib.assertTrue(Machine.interrupt().disabled());
			owner = thread;
			getThreadState(thread).acquire(this);
		}

		public KThread nextThread() {
			Lib.assertTrue(Machine.interrupt().disabled());
			// implement me
			
			if (isEmpty()) return null;
			TreeSet<ThreadState> set = threadQueue.get(donatingPriority);
			ThreadState nextThread = set.pollFirst();
			if (set.isEmpty()) threadQueue.remove(donatingPriority);
			if (threadQueue.isEmpty()) donatingPriority = 0;
			else donatingPriority = threadQueue.lastKey();
			nextThread.belongingQueue = null;
			threadPool.remove(nextThread);

			if (owner != null && transferPriority) {
				getThreadState(owner).ownedQueue.remove(this);
				promoteState(getThreadState(owner));
			}
			owner = nextThread.thread; 
			if (transferPriority) {
				nextThread.ownedQueue.add(this);
				promoteState(getThreadState(owner));
			}
			
			return owner;
		}

		/**
		 * Return the next thread that <tt>nextThread()</tt> would return,
		 * without modifying the state of this queue.
		 * 
		 * @return the next thread that <tt>nextThread()</tt> would return.
		 */
		/*private ThreadState pickNextThread() {
			// implement me
			Lib.assertTrue(Machine.interrupt().disabled());
			if (isEmpty()) return null;
			TreeSet<ThreadState> set = threadQueue.get(donatingPriority);
			ThreadState ret = set.pollLast();
			if (set.isEmpty()) threadQueue.remove(donatingPriority);
			if (threadQueue.isEmpty()) donatingPriority = 0;
			else donatingPriority = threadQueue.lastKey();
			
			ret.belongingQueue = null;
			threadPool.remove(ret);
			return ret;
		}*/

		public void print() {
			Lib.assertTrue(Machine.interrupt().disabled());
			// implement me (if you want)
			
			System.out.print('['); 
			for (Entry<Integer, TreeSet<ThreadState>> entry : threadQueue.entrySet()) {
				TreeSet<ThreadState> set = entry.getValue();
				for (ThreadState state : set)
					System.out.print("(" + entry.getKey() + "," + state.toString() + "),");
			}
			System.out.println(']');
		}

		/**
		 * <tt>true</tt> if this queue should transfer priority from waiting
		 * threads to the owning thread.
		 */
		protected KThread owner;
		protected TreeMap<Integer, TreeSet<ThreadState>> threadQueue;
		protected TreeMap<ThreadState, Integer> threadPool;
		protected int donatingPriority;
		public boolean transferPriority;
		
		protected int getPriority() {
			return donatingPriority;
		}
		
		protected boolean isEmpty() {
			return threadQueue.isEmpty();
		}
		
		protected void promote(ThreadState thread) {
			int priority = thread.getEffectivePriority();
			int oldPriority = threadPool.get(thread);

			if (priority == oldPriority) return;
			else {
				threadPool.put(thread, priority);
				remove(oldPriority, thread);
				acquire(priority, thread);
				if (priority > oldPriority) {
					if (priority > donatingPriority) {
						donatingPriority = priority;
						if (transferPriority && owner != null)
							promoteState(getThreadState(owner));
					}
				} else {
					donatingPriority = threadQueue.lastKey();
					if (transferPriority && owner != null)
						promoteState(getThreadState(owner));
				}
			}
		}
		
		protected void promoteState(ThreadState state) {
			int oldDonatedPriority = state.donatedPriority;
			state.donatedPriority = priorityMinimum;
			for (PriorityQueue queue : state.ownedQueue) {
				int priority = queue.getPriority();
				if (priority > state.donatedPriority) state.donatedPriority = priority;
			}
			if (state.donatedPriority != oldDonatedPriority && state.belongingQueue != null)
				state.belongingQueue.promote(state);
		}
	}

	/**
	 * The scheduling state of a thread. This should include the thread's
	 * priority, its effective priority, any objects it owns, and the queue it's
	 * waiting for, if any.
	 * 
	 * @see nachos.threads.KThread#schedulingState
	 */
	protected class ThreadState implements Comparable {
		/**
		 * Allocate a new <tt>ThreadState</tt> object and associate it with the
		 * specified thread.
		 * 
		 * @param thread
		 *            the thread this state belongs to.
		 */
		public ThreadState(KThread thread) {
			this.thread = thread;

			setPriority(priorityDefault);
			
			ownedQueue = new LinkedList<PriorityQueue>();
		}

		/**
		 * Return the priority of the associated thread.
		 * 
		 * @return the priority of the associated thread.
		 */
		public int getPriority() {
			return priority;
		}

		/**
		 * Return the effective priority of the associated thread.
		 * 
		 * @return the effective priority of the associated thread.
		 */
		public int getEffectivePriority() {
			// implement me
			if (donatedPriority > priority) return donatedPriority;
			else return priority;
		}

		/**
		 * Set the priority of the associated thread to the specified value.
		 * 
		 * @param priority
		 *            the new priority.
		 */
		public void setPriority(int priority) {
			if (this.priority == priority)
				return;

			this.priority = priority;

			// implement me
			if (belongingQueue != null)
				belongingQueue.promote(this);
		}

		/**
		 * Called when <tt>waitForAccess(thread)</tt> (where <tt>thread</tt> is
		 * the associated thread) is invoked on the specified priority queue.
		 * The associated thread is therefore waiting for access to the resource
		 * guarded by <tt>waitQueue</tt>. This method is only called if the
		 * associated thread cannot immediately obtain access.
		 * 
		 * @param waitQueue
		 *            the queue that the associated thread is now waiting on.
		 * 
		 * @see nachos.threads.ThreadQueue#waitForAccess
		 */
		public void waitForAccess(PriorityQueue waitQueue) {
			// implement me
			Lib.assertTrue(Machine.interrupt().disabled());
		}

		/**
		 * Called when the associated thread has acquired access to whatever is
		 * guarded by <tt>waitQueue</tt>. This can occur either as a result of
		 * <tt>acquire(thread)</tt> being invoked on <tt>waitQueue</tt> (where
		 * <tt>thread</tt> is the associated thread), or as a result of
		 * <tt>nextThread()</tt> being invoked on <tt>waitQueue</tt>.
		 * 
		 * @see nachos.threads.ThreadQueue#acquire
		 * @see nachos.threads.ThreadQueue#nextThread
		 */
		public void acquire(PriorityQueue waitQueue) {
			// implement me
			Lib.assertTrue(Machine.interrupt().disabled());
			Lib.assertTrue(waitQueue.isEmpty());
			waitQueue.owner = thread;
			if (waitQueue.transferPriority)
				ownedQueue.add(waitQueue);
		}
		
		public String toString() {
			return "(" + incomingTime + "," + thread.toString() + ")";
		}

		/** The thread with which this object is associated. */
		protected KThread thread;
		/** The priority of the associated thread. */
		protected int priority;
		protected int donatedPriority;
		protected long incomingTime;
		
		protected LinkedList<PriorityQueue> ownedQueue;
		protected PriorityQueue belongingQueue;
		@Override
		public int compareTo(Object arg0) {
			ThreadState state = (ThreadState) arg0;
			if (incomingTime == state.incomingTime)
				return thread.compareTo(state.thread);
			else return incomingTime > state.incomingTime ? 1 : -1;
		}
	}
}
