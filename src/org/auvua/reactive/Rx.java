package org.auvua.reactive;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Rx {
	private static Map<Thread,Set<ReactiveDependency>> threadToGetDependenciesMap = new ConcurrentHashMap<Thread,Set<ReactiveDependency>>();
	private static Map<Thread,Set<ReactiveDependency>> threadToSetDependenciesMap = new ConcurrentHashMap<Thread,Set<ReactiveDependency>>();
	
	private static Set<Thread> threadsDetectingGetDependencies = new HashSet<Thread>();
	private static Set<Thread> threadsDetectingSetDependencies = new HashSet<Thread>();
	
	public static <E> RxVar<E> var(E value) {
		return new RxVar<E>(value);
	}
	
	public static <E> RxVar<E> var(Supplier<E> function) {
		return new RxVar<E>(function);
	}
	
	public static RxTask task(Runnable runnable) {
	  return new RxTask(runnable);
	}
	
	/**
	 * Performs a runnable operation, treating all RxVar.set() operations as occurring simultaneously,
	 * so as not to perform redundant reactive updates.
	 * @param runnable the activity to run
	 */
	public static void doSync(Runnable runnable) {
		startDetectingSetDependencies();
		runnable.run();
		stopDetectingSetDependencies();
		
		Set<ReactiveDependency> currentDependees = getThreadLocalSetDependenciesAndClear();
		if(currentDependees == null) {
			System.out.println("currentDependees null in " + Thread.currentThread());
		}
		Set<ReactiveDependency> currentDependents = new HashSet<ReactiveDependency>();
		Set<ReactiveDependency> dependencyQueue = new LinkedHashSet<ReactiveDependency>();
		Set<ReactiveDependency> temp;
		
		while(!currentDependees.isEmpty()) {
			for(ReactiveDependency dependee : currentDependees) {
				for(ReactiveDependency dependent : dependee.getParents()) {
					currentDependents.add(dependent);
					// Force the dependency to come after its child it in the processing queue
					dependencyQueue.remove(dependent);
					dependencyQueue.add(dependent);
				}
			}
			// Use parents as children for next iteration
			temp = currentDependees;
			currentDependees = currentDependents;
			currentDependents = temp;
			currentDependents.clear();
		}
		
		// Update all dependencies, which are now sorted topographically
		for(ReactiveDependency dep : dependencyQueue) {
			dep.update();
		}
	}
	
	public static void startDetectingGetDependencies() {
		Thread currThread = Thread.currentThread();
		threadsDetectingGetDependencies.add(currThread);
		threadToGetDependenciesMap.put(currThread, new HashSet<ReactiveDependency>());
	}
	
	public static void startDetectingSetDependencies() {
		Thread currThread = Thread.currentThread();
		threadsDetectingSetDependencies.add(currThread);
		threadToSetDependenciesMap.put(currThread, new HashSet<ReactiveDependency>());
	}
	
	public static void stopDetectingGetDependencies() {
		threadsDetectingGetDependencies.remove(Thread.currentThread());
	}
	
	public static void stopDetectingSetDependencies() {
		threadsDetectingSetDependencies.remove(Thread.currentThread());
	}
	
	public static void addThreadLocalGetDependency(ReactiveDependency dep) {
		threadToGetDependenciesMap.get(Thread.currentThread()).add(dep);
	}
	
	public static void addThreadLocalSetDependency(ReactiveDependency dep) {
		threadToSetDependenciesMap.get(Thread.currentThread()).add(dep);
	}
	
	public static boolean isDetectingGetDependencies() {
		return threadsDetectingGetDependencies.contains(Thread.currentThread());
	}
	
	public static boolean isDetectingSetDependencies() {
		return threadsDetectingSetDependencies.contains(Thread.currentThread());
	}
	
	public static Set<ReactiveDependency> getThreadLocalGetDependenciesAndClear() {
		Set<ReactiveDependency> threadLocalDependees = threadToGetDependenciesMap.get(Thread.currentThread());
		threadToGetDependenciesMap.put(Thread.currentThread(), new HashSet<ReactiveDependency>());
		return threadLocalDependees;
	}
	
	public static Set<ReactiveDependency> getThreadLocalSetDependenciesAndClear() {
		Set<ReactiveDependency> threadLocalDependees = threadToSetDependenciesMap.get(Thread.currentThread());
		threadToSetDependenciesMap.put(Thread.currentThread(), new HashSet<ReactiveDependency>());
		return threadLocalDependees;
	}
	
}
