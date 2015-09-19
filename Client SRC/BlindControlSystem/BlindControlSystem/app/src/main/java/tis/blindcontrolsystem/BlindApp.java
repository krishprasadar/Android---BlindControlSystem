package tis.blindcontrolsystem;

import android.app.Application;

import java.util.Map;

/**
 * Created by Krishna on 3/15/2015.
 */
public class BlindApp extends Application{
    private String globalIP;
    private String globalPORT;
    private Integer globalTempThreshold = 2;
    private Map<String,Map<String,String>> ruleList;

    public enum variables
    {
        Temperature("temperature"),
        Ambient("ambient"),
        Blind("blind");

        private String val;

        private variables(String val) {
            this.val = val;
        }
        @Override
        public String toString() {
            return val;
        }
    }
    public enum tempLinguisticTerm
    {
        freezing,
        cold,
        comfort,
        warm,
        hot
    }

    public enum ambLinguisticTerm {
        dark,
        dim,
        bright
    }

    public enum blindLinguisticTerm {
        open,
        half,
        close
    }

    public String getGlobalIP() {
        return globalIP;
    }

    public void setGlobalIP(String str) {
        globalIP = str;
    }

    public String getGlobalPORT() {
        return globalPORT;
    }

    public void setGlobalPORT(String str) {
        globalPORT = str;
    }

    public Integer getGlobalTempThreshold() {
        return globalTempThreshold;
    }

    public void setGlobalTempThreshold(Integer str) {
        globalTempThreshold = str;
    }

    public Map<String,Map<String,String>> getRuleList() {
        return ruleList;
    }

    public void setRuleList(Map<String,Map<String,String>> str) {
        ruleList = str;
    }


}
