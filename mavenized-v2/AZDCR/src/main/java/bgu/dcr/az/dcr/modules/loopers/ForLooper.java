/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.loopers;

import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.conf.utils.ConfigurationUtils;
import bgu.dcr.az.execs.exceptions.PanicException;
import bgu.dcr.az.execs.exps.exe.Looper;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author User
 */
@Register("for-loop")
public class ForLooper implements Looper {

    private Map<Class, Configuration> cachedConfigurations = new WeakHashMap<>();

    private Integer count = null;
    private Looper innerLooper = null;
    private String runVar = null;
    private Double startValue = null;
    private Double endValue = null;
    private Double tickSizeValue = null;
    private Integer repeatCountValue = 1;

    /**
     * @propertyName looper
     * @return the inner looper, if no inner loopers defined returns
     * SingleExecutionLooper
     */
    public Looper getInnerLooper() {
        return innerLooper;
    }

    public void setInnerLooper(Looper looper) {
        this.innerLooper = looper;
    }

    /**
     * @propertyName start
     * @return
     */
    public Double getStartValue() {
        return startValue;
    }

    public void setStartValue(Double initialValue) {
        count = null;
        this.startValue = initialValue;
    }

    /**
     * @propertyName end
     * @return
     */
    public Double getEndValue() {
        return endValue;
    }

    public void setEndValue(Double finalValue) {
        count = null;
        this.endValue = finalValue;
    }

    /**
     * @propertyName tick-size
     * @return
     */
    public Double getTickSizeValue() {
        return tickSizeValue;
    }

    public void setTickSizeValue(Double stepValue) {
        count = null;
        this.tickSizeValue = stepValue;
    }

    /**
     * @propertyName repeat-count
     * @return
     */
    public Integer getRepeatCountValue() {
        return repeatCountValue;
    }

    public void setRepeatCountValue(Integer repeatCountValue) {
        count = null;
        this.repeatCountValue = repeatCountValue == null ? 1 : repeatCountValue;
    }

    @Override
    public int count() {
        if (count == null) {
            count = 1 + (int) Math.round((endValue - startValue) / tickSizeValue);
            count *= repeatCountValue;
        }

        return innerLooper == null ? count : count * innerLooper.count();
    }

    @Override
    public void configure(int i, Object o) {
        try {
            checkForLoopValues();
            final String runningVariableValue = Double.toString(getRunningVariableValue(i));

            if (runVar != null) {
                final Configuration conf = cachedConfigurations.computeIfAbsent(o.getClass(), ConfigurationUtils::get);
                if (conf.get(runVar) != null) {
                    conf.configureProperty(o, runVar, runningVariableValue);
                }
            }

            if (innerLooper != null) {
                innerLooper.configure(i % innerLooper.count(), o);
            }
        } catch (ConfigurationException ex) {
            throw new PanicException("problem configuring variable " + runVar, ex);
        }
    }

    private void checkForLoopValues() throws ConfigurationException {
        if (startValue == null) {
            throw new ConfigurationException("start property must be declared in order to perform for-loop");
        }

        if (endValue == null) {
            throw new ConfigurationException("end property must be declared in order to perform for-loop");
        }

        if (tickSizeValue == null) {
            throw new ConfigurationException("tick-size property must be declared in order to perform for-loop");
        }

        if (endValue < startValue) {
            throw new ConfigurationException("end must be greater than start in order to perform for-loop");
        }

        if (tickSizeValue <= 0) {
            throw new ConfigurationException("tick-size must be greater 0 in order to perform for-loop");
        }
    }

    @Override
    public String toString() {
        return "for (" + runVar + "=" + startValue + "; " + runVar + " <= " + endValue + "; " + runVar + " += " + tickSizeValue + ") do " + repeatCountValue + " times.";
    }

    /**
     * @propertyName run-var
     * @return
     */
    @Override
    public String getRunningVariableName() {
        return runVar;
    }

    public void setRunningVariableName(String propertyName) {
        this.runVar = propertyName;
    }

    @Override
    public double getRunningVariableValue(int i) {
        int currI = innerLooper == null ? i : i / innerLooper.count();
        currI = currI / repeatCountValue;
        Double value = (currI >= count ? endValue : startValue + tickSizeValue * (double) currI);
        return value;
    }

}
