package math;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;

public class MyMath{

    public static List<Integer> rms(List<Integer> x, int step) {
        List<Integer> yOut = new LinkedList();

        for(int i=0; i<x.size();i+=step ){
            int y=0;
            for(int j=0; j<step-1 ;j++ ){
                y += x.get(i+j) * x.get(i+j);
            }
            yOut.add((int)sqrt(y/(step>>3)));
        }

        return yOut;
    }


    List<Integer> xIntegral = new LinkedList<>();
    public double  integralTroptium(Integer xNew, int step){
        double  y = 0;

        return y;
    }


    private List<Integer>  updateAverage(List<Integer> dataInDataCash, int STEP){
        List<Integer> dataInDataCashTmp = new LinkedList<>();
        for(int j = (STEP-1);j<dataInDataCash.size();j+=STEP){
            dataInDataCashTmp.add(dataInDataCash.get(j));
            for(int k=1;k<STEP;k++) {
                dataInDataCashTmp.set(
                        dataInDataCashTmp.size() - 1,
                        dataInDataCashTmp.get(dataInDataCashTmp.size()-1) + dataInDataCash.get(j - k)
                );
            }
            dataInDataCashTmp.set(
                    dataInDataCashTmp.size() - 1,
                    dataInDataCashTmp.get(dataInDataCashTmp.size()-1)/STEP
            );
        }
        dataInDataCash.clear();
        return dataInDataCashTmp;
    }

    private List<Integer>  updateMAX(List<Integer> dataInDataCash, int STEP){
        List<Integer> dataInDataCashTmp = new LinkedList<>();
        for(int j = (STEP-1);j<dataInDataCash.size();j+=STEP){
            dataInDataCashTmp.add(dataInDataCash.get(j));
            for(int k=1;k<STEP;k++) {
                if (dataInDataCashTmp.get(dataInDataCashTmp.size()-1) < dataInDataCash.get(j - k)) {
                    dataInDataCashTmp.set(dataInDataCashTmp.size() - 1, dataInDataCash.get(j - k));
                }
            }
        }
        dataInDataCash.clear();
        return dataInDataCashTmp;
    }
}
