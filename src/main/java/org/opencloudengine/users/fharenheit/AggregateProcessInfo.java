package org.opencloudengine.users.fharenheit;


import org.hyperic.sigar.*;

import java.util.*;

/**
 * Tracks the historical usage for child processes as they come and go to give an estimation of the total resources used
 * by a process and all its children, past and present.
 *
 * @author Greg Hinkle
 * @author John Mazzitelli
 */
public class AggregateProcessInfo extends ProcessInfo {

	private Map<Long, ProcessStats> childProcessStats = new HashMap<Long, ProcessStats>();
	private AggregateProcTime aggregateProcTime;
	private AggregateProcMem aggregateProcMem;
	private AggregateProcCpu aggregateProcCpu;
	private AggregateProcFd aggregateProcFd;

	@Override
	public void refresh() throws RuntimeException {
		// first make sure this process itself is refreshed
		super.refresh();

		try {
			// go through the entire process table and find the children of this parent process
			long[] pids = null;

			try {
				pids = sigar.getProcList();
			} catch (Exception e) { // ignore - SIGAR just wasn't able to get the list of processes for some reason
			}

			if (pids != null) {
				Set<Long> runningPids = new HashSet<Long>();
				for (long pid : pids) {
					runningPids.add(pid);
					try {
						ProcState processProcState = sigar.getProcState(pid);
						if (processProcState.getPpid() == super.pid) {
							ProcessStats ps = childProcessStats.get(pid);
							if (ps == null) {
								ps = new ProcessStats(pid);
								ps.isRunning = true;
								childProcessStats.put(pid, ps);
							}

							ps.refresh(sigar);
						}
					} catch (Exception e) {
						// ignore this process, SIGAR can't get its procState for some reason (permissions? process died?)
					}
				}

				for (ProcessStats ps : childProcessStats.values()) {
					if (!runningPids.contains(ps.childPid))
						ps.isRunning = false;
				}
			}

			List<ProcTime> procTimes = new ArrayList<ProcTime>();
			List<ProcMem> procMems = new ArrayList<ProcMem>();
			List<ProcCpu> procCpus = new ArrayList<ProcCpu>();
			List<ProcFd> procFds = new ArrayList<ProcFd>();

			// get the parent process data first - we'll aggregate the children's data with their parent
			// There are some instances where SIGAR can't get one or more of these (maybe permission issues?).
			// Do not bomb if we can't get one or more of these - we'll just handle nulls appropriately.

			// It is safe to get the prior snapshot as we just called super.refresh
			ProcessInfoSnapshot priorSnaphot = priorSnaphot();

			try {
				procTimes.add(priorSnaphot.getTime());
			} catch (Exception e) {
			}

			try {
				procMems.add(priorSnaphot.getMemory());
			} catch (Exception e) {
			}

			try {
				procCpus.add(priorSnaphot.getCpu());
			} catch (Exception e) {
			}

			try {
				procFds.add(priorSnaphot.getFileDescriptor());
			} catch (Exception e) {
			}

			// now get all the children's data
			for (ProcessStats ps : childProcessStats.values()) {
				procTimes.add(ps.childProcTime);
				// Only add running processes data
				if (ps.isRunning) {
					procMems.add(ps.childProcMem);
					procCpus.add(ps.childProcCpu);
					procFds.add(ps.childProcFd);
				}
			}

			// calculate the aggregate data now
			this.aggregateProcTime = new AggregateProcTime(procTimes);
			this.aggregateProcMem = new AggregateProcMem(procMems);
			this.aggregateProcCpu = new AggregateProcCpu(procCpus);
			this.aggregateProcFd = new AggregateProcFd(procFds);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public AggregateProcTime getAggregateTime() {
		return this.aggregateProcTime;
	}

	public AggregateProcMem getAggregateMemory() {
		return this.aggregateProcMem;
	}

	public AggregateProcCpu getAggregateCpu() {
		return this.aggregateProcCpu;
	}

	public AggregateProcFd getAggregateFileDescriptor() {
		return this.aggregateProcFd;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(super.toString());

		if (this.childProcessStats != null) {
			str.append(", child-pids=" + this.childProcessStats.keySet());
		}

		return str.toString();
	}

	/**
	 * Aggregates {@link ProcTime} data into a single object. This has attributes that are analogous to aggregatable
	 * versions of those attributes found in {@link ProcTime}.
	 */
	public class AggregateProcTime {
		long sys;
		long user;
		long total;

		public long getSys() {
			return this.sys;
		}

		public long getUser() {
			return this.user;
		}

		public long getTotal() {
			return this.total;
		}

		public AggregateProcTime(List<ProcTime> times) {
			for (ProcTime time : times) {
				if (time != null) {
					sys += time.getSys();
					user += time.getUser();
					total += time.getTotal();
				}
			}
		}
	}

	public class AggregateProcCpu {
		private long sys;
		private long user;
		private long total;
		private double percent;

		public long getSys() {
			return this.sys;
		}

		public long getUser() {
			return this.user;
		}

		public long getTotal() {
			return this.total;
		}

		public double getPercent() {
			return percent;
		}

		public AggregateProcCpu(List<ProcCpu> cpus) {
			for (ProcCpu cpu : cpus) {
				if (cpu != null) {
					sys += cpu.getSys();
					user += cpu.getUser();
					total += cpu.getTotal();
					percent += cpu.getPercent();

					// TODO: this looks the same as ProcTime - what's the diff?
					// TODO: what about percent and the others?
				}
			}
		}
	}

	public class AggregateProcMem {
		private long majorFaults;
		private long minorFaults;
		private long pageFaults;
		private long resident;
		private long share;
		private long size;

		public long getMajorFaults() {
			return this.majorFaults;
		}

		public long getMinorFaults() {
			return this.minorFaults;
		}

		public long getPageFaults() {
			return this.pageFaults;
		}

		public long getResident() {
			return this.resident;
		}

		public long getShare() {
			return this.share;
		}

		public long getSize() {
			return this.size;
		}

		public AggregateProcMem(List<ProcMem> mems) {
			for (ProcMem mem : mems) {
				if (mem != null) {
					majorFaults += mem.getMajorFaults();
					minorFaults += mem.getMinorFaults();
					pageFaults += mem.getPageFaults();
					resident += mem.getResident();
					share += mem.getShare();
					size += mem.getSize();
				}
			}
		}
	}

	public class AggregateProcFd {
		private long total;

		public long getTotal() {
			return this.total;
		}

		public AggregateProcFd(List<ProcFd> fds) {
			for (ProcFd fd : fds) {
				if (fd != null) {
					total += fd.getTotal();
				}
			}
		}
	}

	private static class ProcessStats {
		public long childPid;
		public boolean isRunning;
		public ProcTime childProcTime;
		public ProcMem childProcMem;
		public ProcCpu childProcCpu;
		public ProcFd childProcFd;

		public ProcessStats(long myPid) {
			this.childPid = myPid;
		}

		public void refresh(SigarProxy sigar) throws Exception {
			// There are some instances where SIGAR can't get one or more of these (maybe permission issues?).
			// Do not bomb if we can't get one or more of these - we'll just handle nulls appropriately.

			try {
				childProcTime = sigar.getProcTime(this.childPid);
			} catch (Exception e) {
			}

			try {
				childProcMem = sigar.getProcMem(this.childPid);
			} catch (Exception e) {
			}

			try {
				childProcCpu = sigar.getProcCpu(this.childPid);
			} catch (Exception e) {
			}

			try {
				childProcFd = sigar.getProcFd(this.childPid);
			} catch (Exception e) {
			}
		}
	}
}