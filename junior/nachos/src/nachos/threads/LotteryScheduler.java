package nachos.threads;

import java.util.TreeSet;

import nachos.machine.Lib;
import nachos.machine.Machine;
import nachos.threads.PriorityScheduler.PriorityQueue;
import nachos.threads.PriorityScheduler.ThreadState;

/**
 * A scheduler that chooses threads using a lottery.
 * 
 * <p>
 * A lottery scheduler associates a number of tickets with each thread. When a
 * thread needs to be dequeued, a random lottery is held, among all the tickets
 * of all the threads waiting to be dequeued. The thread that holds the winning
 * ticket is chosen.
 * 
 * <p>
 * Note that a lottery scheduler must be able to handle a lot of tickets
 * (sometimes billions), so it is not acceptable to maintain state for every
 * ticket.
 * 
 * <p>
 * A lottery scheduler must partially solve the priority inversion problem; in
 * particular, tickets must be transferred through locks, and through joins.
 * Unlike a priority scheduler, these tickets add (as opposed to just taking the
 * maximum).
 */
public class LotteryScheduler extends PriorityScheduler {
	/**
	 * Allocate a new lottery scheduler.
	 */
	public LotteryScheduler() {
		super();
	}

	/**
	 * Allocate a new lottery thread queue.
	 * 
	 * @param transferPriority
	 *            <tt>true</tt> if this queue should transfer tickets from
	 *            waiting threads to the owning thread.
	 * @return a new lottery thread queue.
	 */
	@Override
	public ThreadQueue newThreadQueue(boolean transferPriority) {
		// implement me
		return new LotteryQueue(transferPriority);
	}
	
	protected class LotteryQueue extends PriorityScheduler.PriorityQueue {

		LotteryQueue(boolean transferPriority) {
			super(transferPriority);
		}
		
		@Override
		public void waitForAccess(KThread thread) {
			Lib.assertTrue(Machine.interrupt().disabled());
			ThreadState state = getThreadState(thread);
			state.incomingTime = Machine.timer().getTime();
			int priority = state.getEffectivePriority();
			this.acquire(priority, state);
			donatingPriority += priority;
			if (transferPriority && owner != null)
				promoteState(getThreadState(owner));
			threadPool.put(state, priority);
			state.belongingQueue = this;
		}
		
		@Override
		public KThread nextThread() {
			Lib.assertTrue(Machine.interrupt().disabled());
			// implement me
			if (isEmpty()) return null;
			TreeSet<ThreadState> set = threadQueue.lastEntry().getValue();
			ThreadState nextThread = set.pollFirst();
			if (set.isEmpty()) threadQueue.pollLastEntry();
			donatingPriority -= nextThread.getEffectivePriority();
			nextThread.belongingQueue = null;
			threadPool.remove(nextThread);

			if (owner != null && transferPriority) {
				getThreadState(owner).ownedQueue.remove(this);
				promoteState(getThreadState(owner));
			}
			owner = nextThread.thread; 
			if (transferPriority) {
				nextThread.ownedQueue.add(this);
				promoteState(nextThread);
			}
			return owner;
		}
		
		@Override
		protected void promote(ThreadState thread) {
			int priority = thread.getEffectivePriority();
			int oldPriority = threadPool.get(thread);

			if (priority == oldPriority) return;
			else {
				threadPool.put(thread, priority);
				remove(oldPriority, thread);
				acquire(priority, thread);
				donatingPriority += priority - oldPriority;
				promoteState(getThreadState(owner));
			}
		}
		
		@Override
		protected void promoteState(ThreadState state) {
			int oldDonatedPriority = state.donatedPriority;
			state.donatedPriority = state.priority;
			for (PriorityQueue queue : state.ownedQueue) {
				int priority = queue.getPriority();
				state.donatedPriority += priority;
			}
			if (state.donatedPriority != oldDonatedPriority && state.belongingQueue != null)
				state.belongingQueue.promote(state);
		}
	}
	
}
