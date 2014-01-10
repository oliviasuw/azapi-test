package bgu.dcr.az.texen;


public abstract class AbstractTestingTickable implements Tickable {

    protected int id;
    protected TickablesExecutorService exec;
    protected boolean terminated = false;
    protected TestResult result;
    protected int numTickables;

    public AbstractTestingTickable(int id, TickablesExecutorService exec, int numTickables, TestResult result) {
        this.id = id;
        this.exec = exec;
        this.numTickables = numTickables;
        this.result = result;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public boolean needTicking() {
        return true;
    }

    @Override
    public void tick() {
        result.numberOfTicks.incrementAndGet();
        result.numberOfTicksPerTickable[id]++;
        _tick();
    }

    protected void requestTick(int id) {
        exec.requestTick(id);
    }

    public abstract void _tick();

}
